package com.damon.main.entity;

import com.damon.main.excelUtil.ExcelColumnAnnotation;
import lombok.Data;

@Data
public class WebsiteChannel {
    @ExcelColumnAnnotation(name = "网站名")
    private String website;

    @ExcelColumnAnnotation(name = "栏目名")
    private String channelPath;

    @ExcelColumnAnnotation(name = "栏目别名")
    private String channel;

    @ExcelColumnAnnotation(name = "别名查找")
    private String viaChannelResult;

    @ExcelColumnAnnotation(name = "栏目名查找")
    private String viaChannPathResult;

}
