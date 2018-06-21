package com.damon.main.excelUtil;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ExcelColumnAnnotation {
    String name() default "";
}
