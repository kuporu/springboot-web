package com.cqupt.springbootweb;

import JSci.maths.statistics.TDistribution;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.springbootweb.bean.*;
import com.cqupt.springbootweb.configuration.FileConfig;
import com.cqupt.springbootweb.controller.FormLayoutController;
import com.cqupt.springbootweb.mapper.GradeMapper;
import com.cqupt.springbootweb.mapper.MybatisPlusUserMapper;
import com.cqupt.springbootweb.mapper.UserMapper;
import com.cqupt.springbootweb.service.FileService;
import com.cqupt.springbootweb.service.GradeService;
import com.cqupt.springbootweb.tool.ESD;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@SpringBootTest
class SpringbootWebApplicationTests {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    FileConfig fileConfig;

    @Autowired
    ESD esd;

    @Autowired
    GradeService gradeService;

    @Autowired
    FileService fileService;

    @Autowired
    GradeMapper gradeMapper;

    @Autowired
    FormLayoutController formLayoutController;

    @Test
    void contextLoads() {
        Long query = jdbcTemplate.queryForObject("select count(*) from host_summary", Long.class);
        log.info("当前表有{}条记录", query);
    }

    @Test
    void mybatisPlus(){
        User user = userMapper.selectById(1L);
        log.info("查询到的用户信息：{}", user);
    }

    @Test
    void config() {
        userMapper.selectById(1);
    }

    @Test
    void pythonTojava () {
        TDistribution td = new TDistribution(23);
        double res = td.inverse(0.999);
        System.out.println(res);
    }

    @Test
    void ESD() {
        Integer[] scores = {12, 13, 15, 16, 20, 18, 19, 16, 14, 20, 19, 18, 90, 100};
        List<Integer> s = new LinkedList<Integer>(){{
            add(20); // 0
            add(13);
            add(15);
            add(16);
            add(20);
            add(18);
            add(19);
            add(16);
            add(16); // 8
            add(20);
            add(19);
            add(18);
            add(19);
            add(90); // 13
            add(16);
            add(19);
            add(20);
            add(100); // 17
            add(13);
            add(15);
            add(16);
            add(20);
            add(18);
            add(19);
            add(16); // 24
        }};
        List<Integer> res = esd.ESDTest(s, 0.05, 10);

//        List<Integer> box = esd.getBox();
        System.out.println(res);
//        System.out.println(box);
    }

    @Test
    void testSe () {
        List<Valid> valids = gradeService.selectValidCount();
    }

    @Test
    void testUser () {
        QueryWrapper<User> totalCountInvalidCountQueryWrapper = new QueryWrapper<>();
        totalCountInvalidCountQueryWrapper.select("total_count", "invalid_count", "user_id");
        List<User> users = userMapper.selectList(totalCountInvalidCountQueryWrapper);
        for (User user: users)
            if (user.getInvalidCount() / (double)user.getTotalCount() > fileConfig.getUserReject())
                gradeService.updateIsValidToZero(user.getUserId());
    }

    @Test
    void selectMean () {
        List<MeanAndSD> meanAndSDS = gradeService.selectMeanAndSd();
    }

    @Test
    void testZScore () {
        // 查找到当前的所有z_scores值
        QueryWrapper<Grade> gradeQueryWrapper = new QueryWrapper<>();
        gradeQueryWrapper.select("z_score").eq("is_valid", 1);
        List<Grade> grades = gradeMapper.selectList(gradeQueryWrapper);
        double minZScore = Integer.MAX_VALUE, maxZScore = Integer.MIN_VALUE;
        // 得到z-scores的最大值、最小值
        for (Grade grade: grades) {
            double zScore = grade.getZScore();
            minZScore = Math.min(minZScore, zScore);
            maxZScore = Math.max(maxZScore, zScore);
        }

        System.out.println("minZScore: " + minZScore + "; maxZscore: " + maxZScore);
    }

    @Test
    void testMos () {
        List<Mos> mosList = gradeService.selectMos();
    }
    
    @Test
    void testGESD () {
        List<Integer> list = new ArrayList<>();
        list.add(98);list.add(96);list.add(96);list.add(93);list.add(94);list.add(95);list.add(94);
        list.add(96);list.add(94);list.add(92);list.add(94);list.add(93);list.add(22);list.add(9);
        esd.ESDTest(list, 0.05, 2);
    }

    @Test
    void testFile () {
        String filename = "file.img";// 文件名
        String[] strArray = filename.split("\\.");
        int suffixIndex = strArray.length -1;
        System.out.println("img" + 1 + "." + strArray[suffixIndex]);
    }

    @Test
    void testSelect () {
        List<ReturnFile> files = fileService.createMos();
    }

    @Test
    void testCreateMos () {
//        List<Integer> pictures = new ArrayList<>();
//        List<List<Integer>> rePictures = new ArrayList<>();
//        List<Double> scores = new ArrayList<>();
//        formLayoutController.createMosData(pictures, rePictures, scores);
//        List<ReturnFile> returnFiles = fileService.createMos();
        formLayoutController.createMosData();
    }

    // 测试正则表达式
    @Test
    void testRE () {
        String a="MRI7.jpg";
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(a);
        System.out.println( m.replaceAll("").trim());
    }

    @Test
    void testCount() {
        System.out.println(fileService.picCount());
    }

    @Test
    void testCountPg() {
        QueryWrapper<User> q = new QueryWrapper<>();
        q.eq("user_id", 28);
        User user = userMapper.selectOne(q);
        System.out.println(user.getPageCount());
    }

}
