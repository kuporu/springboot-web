package com.cqupt.springbootweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.springbootweb.bean.PidForReference;
import com.cqupt.springbootweb.bean.Reference;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReferenceMapper extends BaseMapper<Reference> {

    // 嵌套
    List<PidForReference> selectRefPidsByPid(int pid);
}
