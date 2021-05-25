package com.cqupt.springbootweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.springbootweb.bean.User;
// 除非crud操作特别麻烦，可以建立映射，其他都可以使用BaseMapper中的基础方法

public interface MybatisPlusUserMapper extends BaseMapper<User> {
}
