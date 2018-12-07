package com.damon.entity;

import com.damon.excelUtil.ExcelColumnAnnotation;
import lombok.Data;

@Data
public class WebsiteUrl {
    @ExcelColumnAnnotation(name = "网站名称")
    private String website;

    @ExcelColumnAnnotation(name = "首页地址")
    private String url;

    @ExcelColumnAnnotation(name = "备注")
    private String note;
}
