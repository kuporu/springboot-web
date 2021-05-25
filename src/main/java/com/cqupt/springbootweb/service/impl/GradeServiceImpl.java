package com.cqupt.springbootweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.springbootweb.bean.*;
import com.cqupt.springbootweb.mapper.GradeMapper;
import com.cqupt.springbootweb.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    @Autowired
    GradeMapper gradeMapper;

    @Override
    public List<CountAndPictureId> countAndPictureId() {
        return gradeMapper.selectCount();
    }

    @Override
    public List<SubjectCount> sbCount() {
        return gradeMapper.selectSbcount();
    }

    @Override
    public void updateIsValid(int pId, int uId) {
        getBaseMapper().updateIsValid(pId, uId);
    }

    @Override
    public int totalCount(int uid) {
        return gradeMapper.totalCount(uid);
    }

    @Override
    public List<Valid> selectValidCount() {
        return gradeMapper.selectValidCount();
    }

    @Override
    public void updateIsValidToZero(int uid) {
        gradeMapper.updateIsValidToZero(uid);
    }

    @Override
    public List<MeanAndSD> selectMeanAndSd() {
        return gradeMapper.selectMeanAndSd();
    }

    @Override
    public void updateZScore(double zScore, int picId, int uId) {
        gradeMapper.updateZScore(zScore, picId, uId);
    }

    @Override
    public void updateReScaledScore(double min, double sub) {
        gradeMapper.updateReScaledScore(min, sub);
    }

    @Override
    public List<Mos> selectMos() {
        return gradeMapper.selectMos();
    }
}
