package com.cqupt.springbootweb.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "my.file")
@Data
public class FileConfig {
    private String saveRefPicPath;
    private String savePicPath;
    private int minSubjectCount;
    private double ESDAlpha;
    private double maxOutliers;
    private double userReject;
    private int mosMinSubjectCount;
    private String MOSPath;
}
