package com.cqupt.springbootweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.springbootweb.bean.*;

import java.util.List;

public interface GradeService extends IService<Grade> {
    // 返回**融合后图片id**和其对应的**打分人数**（集合）
    List<CountAndPictureId> countAndPictureId();

    List<SubjectCount> sbCount();

    // 修改grade表中is_valid字段为0
    void updateIsValid(int pId, int uId);

    // 统计当前用户id总评分数
    int totalCount(int uid);

    // 统计所有失效的用户id和其对应的失效数目
    List<Valid> selectValidCount();

    // 将指定用户id下的is_valid字段全设置为0
    void updateIsValidToZero(int uid);

    // 查询当前用户id, 所有评分均值，标准差
    List<MeanAndSD> selectMeanAndSd();

    // 修改grade表中z_score字段
    void updateZScore(double zScore, int picId, int uId);

    // 修改re_scaled_score字段
    void updateReScaledScore(double min, double sub);

    // 查询Mos
    List<Mos> selectMos();
}
