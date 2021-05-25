package com.cqupt.springbootweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.springbootweb.bean.File;
import com.cqupt.springbootweb.bean.PidForReference;
import com.cqupt.springbootweb.bean.ReturnFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<File> {

    //  向picture中插入数据，不包括true_score字段
    //  @Insert("insert into picture (path, name, is_reference) values (#{path}, #{name}, #{is_reference})")
    void insertFile(File file);

    //  向picture表中插入观察者人数，传入图片id，统计人数count
    void updateFile(int id, int count);

    //  修改true_score字段
    void updateTrueScoreByPictureId(int pid, double trueScore);

    // 修改name字段
    void updateNameByPictureId(int pid, String name);

    // 嵌套查询，用于生成mos文档
    List<ReturnFile> createMos();

    // 统计融合后的图像个数
    int picCount();
}
