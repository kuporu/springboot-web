package com.cqupt.springbootweb.controller;

import com.cqupt.springbootweb.bean.File;
import com.cqupt.springbootweb.bean.PidForReference;
import com.cqupt.springbootweb.bean.Reference;
import com.cqupt.springbootweb.bean.ReturnFile;
import com.cqupt.springbootweb.configuration.FileConfig;
import com.cqupt.springbootweb.service.FileService;
import com.cqupt.springbootweb.service.ReferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@Slf4j
public class FormLayoutController {

    @Autowired
    FileService fileService;

    @Autowired
    FileConfig fileConfig;

    @Autowired
    ReferenceService referenceService;

//    List<Integer> rePicIds = new ArrayList<>();
//    List<Integer> picIds = new ArrayList<>();

    @GetMapping("/form_layouts")
    public String form_layouts(String userName) {
        return "form/form_layouts";
    }

    @PostMapping("/upload")
    public String upload(
                         @RequestPart("reference_pictures") MultipartFile[] re_pics,
                         @RequestPart("pictures") MultipartFile[] pics) throws IOException {

        savePicture(re_pics, pics);

        return "redirect:/form_layouts";
    }

    // 批量上传(文件中以数字区分图片)
    @PostMapping("/upload_all")
    public String upload_all(
                            @RequestPart("reference_pictures") MultipartFile[] re_pics,
                            @RequestPart("pictures") MultipartFile[] pics) throws IOException {
        for (MultipartFile pic: pics) {
            // 融合后的图像
            String picName = pic.getOriginalFilename();
            String num1 = re(picName);
            List<MultipartFile> reps = new ArrayList<>();

            // 如果名字匹配，添加到参考图像List集合中
            for (MultipartFile re_pis: re_pics) {
                String reName = re_pis.getOriginalFilename();
                String num2 = re(reName);

                // 如果数字编号相同，则将参考图像加入到对应的融合图像后
                if (num1.equals(num2)) {
                    reps.add((re_pis));
                }
            }

            savePicture(reps.toArray(new MultipartFile[]{}), new MultipartFile[]{pic});
        }


        return "redirect:/form_layouts";
    }

    // 返回字符串中的数字
    private String re(String pathName) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(pathName);
        return m.replaceAll("").trim();
    }

    /**
     * 获取文件后缀名
     * @param fileName 文件名称
     * @return 文件后缀名
     */
    private String getSuffix (String fileName) {
        String[] strArray = fileName.split("\\.");
        int suffixIndex = strArray.length -1;
        return strArray[suffixIndex];
    }

    /**
     * 保存文件到对应的文件夹，数据库
     * @param files 需要保存的文件
     * @param filePath 文件夹路径
     * @param isReference 是否为参考文件
     * @throws IOException 字节流异常
     */
    private void savePictureToDataBaseAndExplorer (MultipartFile[] files, String filePath, int isReference, List<Integer> rePicIds, List<Integer> picIds) throws IOException {
        for (MultipartFile multipartFile: files) {
            if (!multipartFile.isEmpty()) {
                //  获取后缀名
                String fileName = multipartFile.getOriginalFilename();

                //  将参考图片保存在数据库中
                File file = new File();
                file.setPath(filePath);
                file.setIsReference(isReference);
                fileService.insert(file);

                // 通过picture_id字段拼接图片名（通过file.getPictureId()可自动获得自增长id）
                assert fileName != null;
                String name = "img" + file.getPictureId() + "." + getSuffix(fileName);

                // 修改picture表中图片名
                fileService.updateNameByPictureId(file.getPictureId(), name);

                //  将参考图片保存在磁盘中
                multipartFile.transferTo(new java.io.File(filePath + name));



                // 保存picture_id到对应的集合中，用于向reference表中添加数据
                if (isReference == 1)
                    rePicIds.add(file.getPictureId());
                if (isReference == 0)
                    picIds.add(file.getPictureId());
            }
        }
    }

    /**
     * 将融合后的图片id，MOS，参考图像id出入指定txt文件中（覆盖，避免记录最新值，但增加了运行开销）
     */
    @GetMapping("/createMosData")
    public String createMosData () {
        List<ReturnFile> returnFiles = fileService.createMos();

        //  写入txt文件
        try {
            FileWriter fileWriter = new FileWriter(fileConfig.getMOSPath()); //创建文件，但需要路径
            for (ReturnFile returnFile: returnFiles) {
                fileWriter.write(returnFile.getPid() + " ");
                fileWriter.write(String.format("%.4f", returnFile.getScore()) + " ");
                int n = returnFile.getRePids().size() - 1;
                for (int i = 0; i < n; i++) {
                    fileWriter.write(returnFile.getRePids().get(i).getPid() + " ");
                }
                fileWriter.write(returnFile.getRePids().get(n).getPid() + "\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "main";
    }
    // 保存（一个）融合后的图像和（多个）对应参考图像
    private void savePicture(MultipartFile[] refs, MultipartFile[] pics) throws IOException {

        List<Integer> rePicIds = new ArrayList<>();
        List<Integer> picIds = new ArrayList<>();

        //  保存参考图像
        savePictureToDataBaseAndExplorer(refs, fileConfig.getSaveRefPicPath(), 1, rePicIds, picIds);
        //  保存融合后的图像
        savePictureToDataBaseAndExplorer(pics, fileConfig.getSavePicPath(), 0, rePicIds, picIds);

        //  将参考图像和融合后的图像放入reference表中
        for (int re: rePicIds) {
            for (int pic: picIds) {
                Reference reference = new Reference();
                reference.setPictureId(pic);
                reference.setRefPictureId(re);
                referenceService.save(reference);
            }
        }
    }

}
