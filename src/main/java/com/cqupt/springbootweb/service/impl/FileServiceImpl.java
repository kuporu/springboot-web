package com.cqupt.springbootweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.springbootweb.bean.File;
import com.cqupt.springbootweb.bean.ReturnFile;
import com.cqupt.springbootweb.mapper.FileMapper;
import com.cqupt.springbootweb.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    @Autowired
    FileMapper fileMapper;

    @Override
    public void insert(File file) {
        fileMapper.insertFile(file);
    }

    @Override
    public void update(int id, int count) {
        fileMapper.updateFile(id, count);
    }

    @Override
    public void updateTrueScoreByPictureId(int pid, double trueScore) {
        fileMapper.updateTrueScoreByPictureId(pid, trueScore);
    }

    @Override
    public void updateNameByPictureId(int pid, String name) {
        fileMapper.updateNameByPictureId(pid, name);
    }

    @Override
    public List<ReturnFile> createMos() {
        return fileMapper.createMos();
    }

    @Override
    public int picCount() {
        return fileMapper.picCount();
    }

}
