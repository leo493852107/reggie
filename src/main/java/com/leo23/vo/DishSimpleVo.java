package com.leo23.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
public class DishSimpleVo {

    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("菜品名称")
    private String name;

    @ExcelProperty("菜品分类id")
    private Long categoryId;

    @ExcelProperty("菜品价格")
    private BigDecimal price;

    @ExcelProperty("商品码")
    private String code;

    @ExcelProperty("图片")
    private String image;

    @ExcelProperty("描述信息")
    private String description;

    @ExcelProperty("0 停售 1 起售")
    private Integer status;

    @ExcelProperty("顺序")
    private Integer sort;

    @DateTimeFormat(value = "插入时间")
    private LocalDateTime createTime;

    @DateTimeFormat("更新时间")
    private LocalDateTime updateTime;

    @ExcelProperty("创建用户")
    private Long createUser;

    @ExcelProperty("更新用户")
    private Long updateUser;

    @ExcelProperty("是否删除")
    private Integer isDeleted;

}
