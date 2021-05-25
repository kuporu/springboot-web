package com.cqupt.springbootweb.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@TableName(value = "user")
public class User {
//    @TableField(exist = false)表示当前属性在数据表中不存在，属于临时属性

//    以下用于数据库连接属性
    @TableId(value = "user_id")
    private int userId;
//    @TableField(value = "user_name")
    private String userName;
    private String password;
    private int totalCount;
    private int invalidCount;
    private int pageCount;
}
