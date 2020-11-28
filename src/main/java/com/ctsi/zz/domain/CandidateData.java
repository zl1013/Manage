package com.ctsi.zz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * Description:
 * Created by 乍暖还寒 on 2020/8/31 12:17
 * Version 1.0
 */
@Data
public class CandidateData {

    @ExcelProperty(value = "姓名",index = 0)
    private String name;

    @ExcelProperty(value = "邮箱",index = 1)
    private String email;

    @ExcelProperty(value = "属性",index = 2)
    private String type;

    @ExcelProperty(value = "年龄",index = 3)
    private Integer age;

    @ExcelProperty(value = "学历",index = 4)
    private String education;

    @ExcelProperty(value = "手机",index = 5)
    private String phone;

}
