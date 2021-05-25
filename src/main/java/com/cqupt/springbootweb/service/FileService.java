package com.cqupt.springbootweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.springbootweb.bean.File;
import com.cqupt.springbootweb.bean.ReturnFile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileService extends IService<File> {
    void insert(File file);

    void update(int id, int count);

    //  修改true_score字段
    void updateTrueScoreByPictureId(int pid, double trueScore);

    // 修改name字段
    void updateNameByPictureId(int pid, String name);

    // 嵌套查询，用于生成mos文档
    List<ReturnFile> createMos();

    // 查询融合后图像个数
    int picCount();
}
