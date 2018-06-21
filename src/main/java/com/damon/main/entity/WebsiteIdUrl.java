package com.damon.main.entity;

import com.damon.main.excelUtil.ExcelColumnAnnotation;
import lombok.Data;

@Data
public class WebsiteIdUrl {

    @ExcelColumnAnnotation(name = "id")
    private String id;

    @ExcelColumnAnnotation(name = "website")
    private String website;

    @ExcelColumnAnnotation(name = "address")
    private String url;
}
