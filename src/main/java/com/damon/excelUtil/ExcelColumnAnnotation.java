package com.damon.excelUtil;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ExcelColumnAnnotation {
    String name() default "";
    //这里解释一下，实体类有些属性使用了该注解，但不代表导入excel的时候excel中需要该列名，
    //因为导出excel的时候,我需要使用实体类中的属性，因此我给一些属性也加上了注解
    //但是这些字段并不是导入excel的时候需要的列名
    boolean isExcelColumn() default true;
    //读取excel的时候，是否一定需要该列
    boolean required() default true;
    //是否数据库中的字段，下载的时候需要用到该属性
    boolean isDbColumn() default true;
    //下载的时候是否需要返回该字段
    boolean shouldDownload() default true;
}
