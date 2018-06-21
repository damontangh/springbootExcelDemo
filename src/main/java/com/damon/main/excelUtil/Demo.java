package com.damon.main.excelUtil;

import com.damon.main.entity.WebsiteTemp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) throws Exception {
        List<WebsiteTemp> websiteTemps = ExcelUtil.mapExcelToPOJO("C:\\Users\\lenovo\\Desktop\\Test.xls",WebsiteTemp.class);
        System.out.println(websiteTemps);
    }
}
