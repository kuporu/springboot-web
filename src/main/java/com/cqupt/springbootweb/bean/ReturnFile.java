package com.cqupt.springbootweb.bean;

import lombok.Data;

import java.util.List;

@Data
public class ReturnFile {
    private double score;
    private int pid;
    private List<PidForReference> rePids;
}
