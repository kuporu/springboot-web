package com.cqupt.springbootweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.springbootweb.bean.User;

public interface UserService extends IService<User> {

    // 这个方法是自己写的,其余的都继承自IService
    public void insertUser(User user);

    public User selectByUserNameAndPassword(User user);

    // 修改total_count字段
    void updateTotalCount(int uid, int count);

    // 修改invalid_count字段
    void updateInvalidCount(int uid, int invalidCount);

    // 保存当前用户页码
    void updatePageCount(int uid, int pageCount);
}
