package com.cqupt.springbootweb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.springbootweb.bean.*;
import com.cqupt.springbootweb.configuration.FileConfig;
import com.cqupt.springbootweb.mapper.FileMapper;
import com.cqupt.springbootweb.mapper.GradeMapper;
import com.cqupt.springbootweb.mapper.UserMapper;
import com.cqupt.springbootweb.service.FileService;
import com.cqupt.springbootweb.service.GradeService;
import com.cqupt.springbootweb.service.UserService;
import com.cqupt.springbootweb.tool.ESD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ScoreController {

    @Autowired
    GradeService gradeService;

    @Autowired
    GradeMapper gradeMapper;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    FileConfig fileConfig;

    @Autowired
    FileService fileService;

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ESD esd;

    /**
     * 主函数
     */

    @GetMapping("refresh")
    public String refresh () {
        addSubjectSCountToPicture();
        deleteInvalid();
        vaildCount();
        subjectsReject();
        toZScore();
        reScaledScore();
        MOS();
        return "main";
    }

    /**
     * 添加每张图片评分人数到picture表中
     */
    private void addSubjectSCountToPicture () {

        //  从分数表中返回**融合后图片id**和其对应的**打分人数**（集合）
        List<CountAndPictureId> countAndPictureIds = gradeService.countAndPictureId();

        int pictureId, count;
        for (CountAndPictureId countAndPictureId: countAndPictureIds) {
            pictureId = countAndPictureId.getPictureId();
            count = countAndPictureId.getCount();

            //  将**打分人数**保存在picture表中**图片id**所在的数据项
            fileService.update(pictureId, count);
        }
    }

    /**
     * 观察者拒绝
     */
    private void subjectsReject () {
        QueryWrapper<User> countQw = new QueryWrapper<>();
        countQw.select("total_count", "invalid_count", "user_id");
        List<User> users = userMapper.selectList(countQw);
        for (User user: users)
            if (user.getInvalidCount() / (double)user.getTotalCount() > fileConfig.getUserReject())
                gradeService.updateIsValidToZero(user.getUserId());
    }

    /**
     * 查询用户所有异常评分次数，添加到用户表中
     */
    private void vaildCount () {
        List<Valid> valids = gradeService.selectValidCount();
        for (Valid valid: valids) {
            userService.updateInvalidCount(valid.getUid(), valid.getCount());
        }
    }

    /**
     * 计算z-score分数
     */
    private void toZScore () {

        // 对应用户所有评分的均值、方差（集合）group by user_id
        List<MeanAndSD> meanAndSDS = gradeService.selectMeanAndSd();

        // 查找当前用户的所有picture_id
        for (MeanAndSD meanAndSD: meanAndSDS) {
            QueryWrapper<Grade> gradeQueryWrapper = new QueryWrapper<>();
            gradeQueryWrapper.select("score", "picture_id").eq("user_id", meanAndSD.getUserId());
            List<Grade> grades = gradeMapper.selectList(gradeQueryWrapper);
            for (Grade grade: grades) {
                double zScore = (grade.getScore() - meanAndSD.getMean()) / meanAndSD.getSd();
                gradeService.updateZScore(zScore, grade.getPictureId(), meanAndSD.getUserId());
            }
        }
    }

    /**
     * 缩放z-score分数
     */
    private void reScaledScore () {

        // 查找到当前的所有z_scores值
        QueryWrapper<Grade> gradeQueryWrapper = new QueryWrapper<>();
        gradeQueryWrapper.select("z_score").eq("is_valid", 1);
        List<Grade> grades = gradeMapper.selectList(gradeQueryWrapper);
        double minZScore = Integer.MAX_VALUE, maxZScore = Integer.MIN_VALUE;
        // 得到z-scores的最大值、最小值
        for (Grade grade: grades) {
            double zScore = grade.getZScore();
            minZScore = Math.min(minZScore, zScore);
            maxZScore = Math.max(maxZScore, zScore);
        }

        gradeService.updateReScaledScore(minZScore, maxZScore - minZScore);
    }

    /**
     * 删除异常值
     */
    private void deleteInvalid () {

        //  从分数表中返回**融合后图片id**，**打分人数**，**分数均值**，**分数标准差**（集合）group by picture_id
        List<SubjectCount> meanAndStandardDeviations = gradeService.sbCount();
        for (SubjectCount meanAndStandardDeviation: meanAndStandardDeviations) {

            //  融合后图片评分人数超过**给定值**才运行
            if (meanAndStandardDeviation.getSubjectCount() >= fileConfig.getMinSubjectCount()) {
                QueryWrapper<Grade> gradeQueryWrapper = new QueryWrapper<>();
                gradeQueryWrapper.select("score", "user_id").eq("picture_id",meanAndStandardDeviation.getPictureId());

                //  从grade表中返回对应融合后图片的**分数**，**用户id**（集合）
                List<Grade> grades = gradeMapper.selectList(gradeQueryWrapper);
                List<Integer> userIdTemps = new ArrayList<>();
                List<Integer> scores = new ArrayList<>();
                for (Grade grade: grades) {
                    scores.add(grade.getScore());
                    userIdTemps.add(grade.getUserId());
                }

                //  获取异常值的**用户id**
                List<Integer> uIds = esd.ESDTest(scores, fileConfig.getESDAlpha(), (int) Math.floor(fileConfig.getMaxOutliers() * scores.size()));

                //  将**融合后图片id**和**异常值用户id**对应的**is_valid**字段设置为0
                for (int i: uIds)

                    // 异常值剔除
                    gradeService.updateIsValid(meanAndStandardDeviation.getPictureId(), userIdTemps.get(i));
            }
        }
    }

    /**
     * 计算MOS分数保存至picture，true_score中
     */
    private void MOS () {
        List<Mos> mosList = gradeService.selectMos();
        for (Mos mos: mosList) {
            if (mos.getReScoreCount() > fileConfig.getMosMinSubjectCount()) {
                fileService.updateTrueScoreByPictureId(mos.getPid(), mos.getMeanReScore());
            }
        }
    }
}
