package com.damon.entity;

import com.damon.excelUtil.ExcelColumnAnnotation;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class WebsiteTemp {

    @ExcelColumnAnnotation(name = "网站名")
    private String websiteName;

    @ExcelColumnAnnotation(name = "栏目名")
    private String oldChannelPath;

    @ExcelColumnAnnotation(name = "栏目别名")
    private String channel;

    @ExcelColumnAnnotation(name = "新栏目名")
    private String newChannelPath;

}
