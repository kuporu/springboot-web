package com.cqupt.springbootweb.tool;


import JSci.maths.statistics.TDistribution;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class ESD {
    private int maxIdx;

    /**
     * 计算均值
     * @param list 输入列表
     * @return 返回列表均值
     */
    private double mean(List<Integer> list) {
        double sum = 0.0;
        for (int li: list) {
            sum += li;
        }

        return sum / list.size();
    }

    /**
     * 计算标准差
     * @param list 输入列表
     * @return 返回标准差
     */
    private double std(List<Integer> list) {
        double mean = mean(list);
        double temp = 0.0;
        for (int li: list) {
            temp += (li - mean) * (li - mean);
        }
        return Math.sqrt(temp / list.size());
    }

    /**
     * 计算统计量，确定统计量对应下标
     * @param list 输入列表
     * @return 返回统计量
     */
    private double grubbs_stat (List<Integer> list) {
        int idx = 0, maxIdx = 0;
        double maxValue = -1.0;
        for (int it: list) {
            double mid = Math.abs(it - mean(list));
            if (maxValue < mid) {
                maxValue = mid;
                maxIdx = idx;
            }
            idx++;
        }
        this.maxIdx = maxIdx;
        return maxValue / std(list);
    }

    /**
     * 计算临界区域
     * @param size 样本大小（list长度）
     * @param alpha 显著性水平
     * @return 返回临界区域
     */
    private double calculate_critical_value (int size, double alpha){
        TDistribution td = new TDistribution(size - 2);
        double tDist = td.inverse(1 - alpha / (2 * size));
        double numerator = (size - 1) * tDist;
        double denominator = Math.sqrt(size) * Math.sqrt(size - 2 + tDist * tDist);
        return numerator / denominator;
    }

    /**
     * 比较统计量和临界区域大小
     * @param Gs 统计量
     * @param Gc 临界区域
     * @return 统计量大于临界区域返回true，否者返回false
     */
    private boolean checkGValues (double Gs, double Gc) {
        return Gs > Gc;
    }

    /**
     * 输入列表，返回剔除异常值后的列表
     * @param list 原始列表
     * @param alpha 显著性水平
     * @param maxOutliers 异常值上限
     * @return 返回剔除异常值后的列表
     */
    public List<Integer> ESDTest (List<Integer> list, double alpha, int maxOutliers) {
        List<Integer> box = new ArrayList<>();
        List<Integer> res = new ArrayList<>(list);
        for(int i = 0; i < maxOutliers; i++) {

            double Gcritical = calculate_critical_value(list.size(), alpha);
            double Gstat = grubbs_stat(list); // 同时得到maxIdx

            if (checkGValues(Gstat, Gcritical)) {
                for (int j = 0; j < res.size(); j++) {
                    if (res.get(j).equals(list.get(maxIdx))) {
                        box.add(j);
                        res.set(j, -1);
                        break;
                    }
                }
            }

            list.remove(maxIdx);
        }

        return box;
    }

}
