package com.damon.excelUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private String nothing;


    @ExcelColumnAnnotation(name = "姓名")
    private String name;

    private double nothing3;

    @ExcelColumnAnnotation(name = "年龄")
    private int age;

    @ExcelColumnAnnotation(name = "身高")
    private double height;

    private int nothing2;

    @ExcelColumnAnnotation(name = "是否单身")
    private boolean ifSingle;

    @ExcelColumnAnnotation(name = "位置")
    private String location;


}
