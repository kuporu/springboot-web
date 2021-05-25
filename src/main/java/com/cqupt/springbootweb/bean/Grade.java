package com.cqupt.springbootweb.bean;

import lombok.Data;

@Data
public class Grade {
    private int score;
    private int pictureId;
    private int userId;
    private int isValid;
    private double zScore;
    private double reScaledScore;

}
