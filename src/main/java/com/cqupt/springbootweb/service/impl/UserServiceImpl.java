package com.cqupt.springbootweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.springbootweb.bean.User;
import com.cqupt.springbootweb.mapper.UserMapper;
import com.cqupt.springbootweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// service表示业务逻辑组件
// UserServiceImpl中调用UserMapper中的方法，但UserMapper中的方法都规定好了
// @Service注解是标注在实现类上的，因为@Service是把spring容器中的bean进行实例化，
// 也就是等同于new操作，只有实现类是可以进行new实例化的，而接口则不能，所以是加在实现类上的。

// 参考https://gitee.com/baomidou/mybatis-plus/blob/3.0/mybatis-plus-extension/src/main/java/com/baomidou/mybatisplus/extension/service/impl/ServiceImpl.java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;

//    自己写的功能
    @Override
    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    @Override
    public User selectByUserNameAndPassword(User user) {
        return userMapper.selectByPasswordAndUserName(user);
    }

    @Override
    public void updateTotalCount(int uid, int count) {
        userMapper.updateTotalCount(uid, count);
    }

    @Override
    public void updateInvalidCount(int uid, int invalidCount) {
        userMapper.updateInvalidCount(uid, invalidCount);
    }

    @Override
    public void updatePageCount(int uid, int pageCount) {
        userMapper.updatePageCount(uid, pageCount);
    }

}
