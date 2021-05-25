package com.cqupt.springbootweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.springbootweb.bean.Reference;
import com.cqupt.springbootweb.mapper.ReferenceMapper;
import com.cqupt.springbootweb.service.ReferenceService;
import org.springframework.stereotype.Service;

@Service
public class ReferenceImpl extends ServiceImpl<ReferenceMapper, Reference> implements ReferenceService {
}
