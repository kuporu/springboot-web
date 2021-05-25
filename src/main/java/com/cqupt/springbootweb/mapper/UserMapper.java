package com.cqupt.springbootweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.springbootweb.bean.User;
import org.apache.ibatis.annotations.Mapper;

// 参考https://baomidou.com/guide/crud-interface.html#chain中Mapper CRUDj接口
@Mapper
public interface UserMapper extends BaseMapper<User>{

    // 插入用户
    void insertUser(User user);

    // 匹配查询登录
    User selectByPasswordAndUserName(User user);

    // 修改total_count字段
    void updateTotalCount(int uid, int count);

    // 修改invalid_count字段
    void updateInvalidCount(int uid, int invalidCount);

    // 保存当前用户页码
    void updatePageCount(int uid, int pageCount);
}
