package com.leo23.utils;

import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Row;

/**
 * excel表格的行高设置及样式
 */
public class CustomRowHeightStyleStrategy extends AbstractRowHeightStyleStrategy {

    // 设置需要隐藏的行号
    Integer rowNum;

    public CustomRowHeightStyleStrategy(int rowNum) {
        this.rowNum = rowNum;
    }

    // 设置表头的行高
    @Override
    protected void setHeadColumnHeight(Row row, int i) {
        //设置表头行高为18
        row.setHeightInPoints(18);
    }

    // 设置内容的行高 relativeRowIndex为行数，索引从0开始
    @Override
    protected void setContentColumnHeight(Row row, int i) {
        if (i + 1 == rowNum) {
            //设置行隐藏
            row.setHeightInPoints(0);
        }
    }
}
