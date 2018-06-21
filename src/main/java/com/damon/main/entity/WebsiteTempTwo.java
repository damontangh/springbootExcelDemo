package com.damon.main.entity;

import com.damon.main.excelUtil.ExcelColumnAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WebsiteTempTwo {

    @ExcelColumnAnnotation(name = "网站名")
    private String websiteName;

    @ExcelColumnAnnotation(name = "地方频道")
    private String partChannel;

    @ExcelColumnAnnotation(name = "栏目名")
    private String oldChannelPath;

    @ExcelColumnAnnotation(name = "栏目别名")
    private String channel;

    @ExcelColumnAnnotation(name = "链接地址")
    private String url;

    @ExcelColumnAnnotation(name = "新地方频道")
    private String newPartChannel;

    @ExcelColumnAnnotation(name = "新栏目名称")
    private String newChannelPath;

    @ExcelColumnAnnotation(name = "新栏目别名")
    private String newChannel;

    @ExcelColumnAnnotation(name = "新URL地址")
    private String newUrl;


}
