package com.cqupt.springbootweb.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@TableName("picture")
@ToString
public class File {
    private int pictureId;
    private String path;
    private String name;
    private float trueScore;
    private int isReference;
    private int subjectsCount;
}
