package com.damon.excelUtil;

import com.damon.entity.WebsiteTemp;

import java.util.List;

public class Demo {
    public static void main(String[] args) throws Exception {
        List<WebsiteTemp> websiteTemps = ExcelUtil.mapExcelToPOJO("C:\\Users\\lenovo\\Desktop\\Test.xls",WebsiteTemp.class);
        System.out.println(websiteTemps);
    }
}
