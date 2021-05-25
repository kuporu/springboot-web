package com.cqupt.springbootweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.springbootweb.bean.File;
import com.cqupt.springbootweb.bean.Grade;
import com.cqupt.springbootweb.bean.Reference;
import com.cqupt.springbootweb.bean.User;
import com.cqupt.springbootweb.mapper.GradeMapper;
import com.cqupt.springbootweb.mapper.ReferenceMapper;
import com.cqupt.springbootweb.mapper.UserMapper;
import com.cqupt.springbootweb.service.FileService;
import com.cqupt.springbootweb.service.GradeService;
import com.cqupt.springbootweb.service.UserService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PictureController {

    @Autowired
    FileService fileService;

    @Autowired
    ReferenceMapper referenceMapper;

    @Autowired
    GradeService gradeService;

    @Autowired
    GradeMapper gradeMapper;

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    private int userId; // 记录用户id
    private int pictureId; // 记录当前页融合后的图片id
    private int pgCount = 0; // 记录当前用户页面 --> 线程问题
    private List<Integer> list = new ArrayList<>(); // 乱序页码使用
    /**
     * 使用分页取出融合图像和对应的参考图像, 用于**第一次界面**显示
     * @param pn 融合图像页数
     * @param rePn 融合图像对应的参考图像页数
     * @param model 向模板发送数据
     * @return 返回评分页面
     */
    @GetMapping("/gallery.html")
    public String dynamic_table(@RequestParam(value = "pn",defaultValue = "1") int pn,
                                @RequestParam(value = "re_pn",defaultValue = "1") int rePn,
                                Model model,
                                HttpSession httpSession)
    {
        userId = ((User) httpSession.getAttribute("login")).getUserId();

        // 伪乱序
        int picCount = fileService.picCount();
        // 一个容器中list只初始化一次（多线程没考虑）----> 决定了评分后就不能再导入图像了
        if (list.size() == 0) {
            for (int i = 0; i < picCount; i++)
                list.add(i + 1);
            int temp;
            for (int i = 0; i < (picCount / 2); i++) {
                if (i % 2 != 0) {
                    temp = list.get(i);
                    list.set(i, list.get(picCount - i - 1));
                    list.set(picCount - i - 1, temp);
                }
            }
        }


        // 获取当前用户的page_count;
        QueryWrapper<User> qu = new QueryWrapper<>();
        qu.eq("user_id", userId);
        User user = userMapper.selectOne(qu);
        pgCount = user.getPageCount();

        pgCount += pn;
        Page<File> userPage = new Page<>(list.get(pgCount - 1), 1); // 乱序只在第一个形参上操作
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_reference",0); // 只查询is_reference = 0（融合图片）的图片
        IPage<File> page = fileService.page(userPage, queryWrapper); // page中现在只有一个file(图片)

        model.addAttribute("count", pgCount);
        model.addAttribute("picCount", picCount);

        // 返回给视图（一张）融合图像
        model.addAttribute("page", page);

        // 返回（多张）对应的参考图像
        pageToRepage(page, model, rePn);

        model.addAttribute("uid", userId);

        // 如果**当前用户**对**当前融合图片**已评分则添加标记，在界面中就不显示评分滑块， 并返回查询到的评分结果
        // 通过判断是否有grade关键字控制滑块的开启
        controlSlider(page, model);

        return "picture/gallery";
    }

    /**
     * 页面按键“<<”, ">>"跳转页面
     * @param pn 控制融合后的图片页数（虚拟），左翻pgCount - 1， 右翻pgCount + 1,
     * @param rePn 控制参考图像页数
     * @param model
     * @return
     */
    @GetMapping("/gallery_two.html")
    public String dynamic_table(@RequestParam(value = "pn") int pn,
                                @RequestParam(value = "re_pn",defaultValue = "1") int rePn,
                                Model model)
    {

        // 伪乱序
        int picCount = fileService.picCount();

        // 获取当前用户的page_count;
        pgCount += pn;
        Page<File> userPage = new Page<>(list.get(pgCount - 1), 1); // 乱序只在第一个形参上操作
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_reference",0); // 只查询is_reference = 0（融合图片）的图片
        IPage<File> page = fileService.page(userPage, queryWrapper); // page中现在只有一个file(图片)


        model.addAttribute("count", pgCount);
        model.addAttribute("picCount", picCount);

        // 返回给视图（一张）融合图像
        model.addAttribute("page", page);

        // 返回（多张）对应的参考图像
        pageToRepage(page, model, rePn);

        model.addAttribute("uid", userId);

        // 如果**当前用户**对**当前融合图片**已评分则添加标记，在界面中就不显示评分滑块， 并返回查询到的评分结果
        controlSlider(page, model);

        return "picture/gallery";
    }


    /**
     * 通过融合后的图像查找参考图像
     * @param page 融合后的图像page
     * @param model 返回给视图的容器
     * @param rePn 当前参考图像页数
     */
    private void pageToRepage(IPage<File> page, Model model, int rePn) {
        // 返回（多张）对应的参考图像
        QueryWrapper<Reference> referenceQueryWrapper = new QueryWrapper<>();
        referenceQueryWrapper.select("ref_picture_id").eq("picture_id", page.getRecords().get(0).getPictureId());

        // 获取得到参考图像的id值
        List<Reference> references = referenceMapper.selectList(referenceQueryWrapper);

        // 将参考图片的id保存在集合中
        List<Integer> pictureIds = new ArrayList<>();
        for (Reference ref: references)
            pictureIds.add(ref.getRefPictureId());

        // 通过id值得到参考图像的所有信息并分页返回
        Page<File> pg = new Page<>(rePn, 2);
        QueryWrapper<File> qw = new QueryWrapper<>();
        qw.in("picture_id", pictureIds);
        IPage<File> rePage = fileService.page(pg, qw);
        model.addAttribute("rePage", rePage);
    }

    /**
     * 控制页面滑块的显示，如果以评分，则不显示滑块（页面通过是否有grade字段判断是否评分）
     * @param page 当前融合后的图像page
     * @param model 放回给视图的容器
     */
    private void controlSlider(IPage<File> page, Model model) {
        pictureId = page.getRecords().get(0).getPictureId();
        QueryWrapper<Grade> gradeQueryWrapper = new QueryWrapper<>();
        gradeQueryWrapper.eq("picture_id", pictureId).eq("user_id", userId);
        List<Grade> gradeList = gradeMapper.selectList(gradeQueryWrapper);
        if (gradeList.size() > 0)
            model.addAttribute("grade",gradeList);
    }

    /**
     * 实现用户打分上传到数据库
     * 使用Ajax调用该方法，可以在原有视图模板的基础上进行修改，而不需要重加载整个视图模板
     * 添加ResponseBody表示不返回视图
     */
    @ResponseBody
    @PostMapping("addScore")
    public void addScore(@RequestBody JSONObject params, HttpSession httpSession){
        int score = params.getInt("score");
        Grade grade = new Grade();
        grade.setScore(score);
        grade.setPictureId(pictureId);
        grade.setUserId(userId);
        grade.setIsValid(1); // 默认为有效

        gradeService.save(grade);
    }

    /**
     * 用于删除用户对某个图片的评分，并重定向到当前界面
     * @param redirectAttributes 设定重定向参数
     * @param pn 当前融合后图片页数
     * @param rePn 当前参考图像页数
     * @return 重定向页面
     */
    @GetMapping("deleteScore")
    public String deleteScore (RedirectAttributes redirectAttributes,
                               @RequestParam("pn") int pn,
                               @RequestParam(value = "re_pn",defaultValue = "1") int rePn) {



        QueryWrapper<Grade> wrapper = new QueryWrapper<>();
        wrapper.eq("picture_id", pictureId).eq("user_id",userId);
        gradeMapper.delete(wrapper);

        redirectAttributes.addAttribute("pn", 0);
        redirectAttributes.addAttribute("re_pn", rePn);
        return "redirect:/gallery_two.html";
    }

    /**
     * 点击提交后触发，保存当前用户评分图像个数，记录当前评分页数
     * @param uid 用户id
     * @return
     */
    @GetMapping("/totalCount")
    public String totalCount (@RequestParam("uId") int uid) {

        // 保存当前用户的评分数目
        int count = gradeService.totalCount(uid);
        userService.updateTotalCount(uid, count);

        // 保存当前页到对应用户数据表
        userService.updatePageCount(uid, pgCount);

        return "main";
    }
}
