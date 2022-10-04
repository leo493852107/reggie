package com.leo23.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.property.StyleProperty;
import com.alibaba.excel.write.merge.OnceAbsoluteMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo23.common.MyUtils;
import com.leo23.dto.DishDto;
import com.leo23.entity.Dish;
import com.leo23.entity.DishFlavor;
import com.leo23.mapper.DishMapper;
import com.leo23.service.DishFlavorService;
import com.leo23.service.DishService;
import com.leo23.utils.CustomRowHeightStyleStrategy;
import com.leo23.utils.CustomSheetWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Value("${reggie.path}")
    private String filePath;
    @Resource
    private DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品基本信息到dish
        this.save(dishDto);

        // 菜品id
        Long dishId = dishDto.getId();
        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存口味到dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息 dish
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 查询菜品对应口味信息 dish_flavor
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);
        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 1.先检查图片是否重新上传，如果重新上传,删除之前图片, 2.更新dish
        Dish oldDish = this.getById(dishDto.getId());
        if (oldDish.getImage() != dishDto.getImage()) {
            MyUtils utils = new MyUtils();
            utils.deleteFile(filePath + oldDish.getImage());
        }
        // 更新dish
        this.updateById(dishDto);

        // 更新dish_flavor， 简单处理就是 1.先删除原先，2.再插入数据
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(wrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void deleteWithFlavor(Long ids) {
        // 删除 dish_flavor
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, ids);
        dishFlavorService.remove(wrapper);

        // 删除 图片文件
        MyUtils utils = new MyUtils();
        utils.deleteFile(filePath + this.getById(ids).getImage());
        // 删除 dish
        super.removeById(ids);
    }

    @Override
    public void downloadExcel(HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream out = null;
        ExcelWriter build = null;
        try {
            // (1)、创建Excel核心对象
            out = response.getOutputStream();
            build = EasyExcel.write(out).build();
            // 创建sheet 0表示：文件中第1个sheet页 || CustomSheetWriteHandler是去除网格线
            WriteSheet sheet = EasyExcel.writerSheet(0, "测试1").registerWriteHandler(
                    new CustomSheetWriteHandler()).build();
            // (2)、创建Excel页面的数据 【手动创建，工作中，这一部分是动态获取的】*/
            List<List<Object>> topicData = new ArrayList<>();
            ArrayList<Object> list1 = new ArrayList<>();
            list1.add("Leo测试");
            topicData.add(list1);
            // 2、表头备注栏【截止日期、板块内容】--secondData
            // 硬代码生成时间和版块内容【工作中，这里是动态生成的】
            String tableTime = "2022/07/05";
            String sectionContent = "用户信息";
            List<List<Object>> secondData = new ArrayList<>();
            List<Object> list2 = new ArrayList<>();
            list2.add("截止日期：" + tableTime + "\n版块内容：" + sectionContent);
            secondData.add(list2);
            // 3、生成数据表格的标题栏内容【这里的元素个数必须与表格保持一致】
            List<List<String>> head = new ArrayList<>();
            List<String> head1 = new ArrayList<>();
            head1.add("用户编号");
            List<String> head2 = new ArrayList<>();
            head1.add("用户编号");
            List<String> head3 = new ArrayList<>();
            head1.add("用户编号");
            head2.add("用户名称");
            head3.add("用户年龄");
            head.add(head1);
            head.add(head2);
            head.add(head3);
            // 4、获取数据表格中的数据
            List<List<Object>> data = getData();
            // 5、获取数据表格的总列数，下面需要用到
            int size = data.get(0).size();

            /**(3)、设置3个表格的样式 */
            //1、设置表头样式【最上面的标题】
            /**
             * 1.1、 合并单元格 【四个参数】
             * 参数1：合并开始的第一行     【0：表示第一行】
             * 参数2：和平结束的最后一行   【0：表示第一行，0-0=0，表示没有合并行】
             * 参数3：合并开始的第一列     【0：表示第一列】
             * 参数4：合并开始的最后一列   【size-1：表示合并的列数与数据表格的列数一致】
             */
            OnceAbsoluteMergeStrategy mergeStrategy = new OnceAbsoluteMergeStrategy(0, 0, 0, size - 1);
            //1.2、设置内容居中
            WriteCellStyle contentStyle = new WriteCellStyle();
            //垂直居中
            contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            //水平居中
            contentStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            WriteFont writeFont = new WriteFont();
            //加粗
            writeFont.setBold(true);
            //字体大小为16
            writeFont.setFontHeightInPoints((short) 16);
            contentStyle.setWriteFont(writeFont);
            // 单元格策略 参数1为头样式【不需要头部，设置为null】，参数2位表格内容样式
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(null, contentStyle);

            //2、设置表头备注栏样式【截止日期、板块内容】
            /**
             * 2.1、合并单元格
             * 两个1表示合并在第二行，且没有合并行
             * 从第一列开始，合并至数据表格的总列数（长度）
             */
            OnceAbsoluteMergeStrategy mergeStrategy2 = new OnceAbsoluteMergeStrategy(1, 1, 0, size - 1);
            //2。1、单元格样式
            WriteCellStyle contentStyle2 = new WriteCellStyle();
            // 设置内容自动换行
            contentStyle2.setWrapped(true);
            // 字体样式
            WriteFont writeFont2 = new WriteFont();
            writeFont2.setFontHeightInPoints((short) 10);
            contentStyle2.setWriteFont(writeFont2);
            HorizontalCellStyleStrategy horizontalCellStyleStrategy2 = new HorizontalCellStyleStrategy(null, contentStyle2);

            //3、设置数据表格的样式
            //  ---------- 头部样式 ----------
            WriteCellStyle headStyle = new WriteCellStyle();
            // 字体样式
            WriteFont headFont = new WriteFont();
            headFont.setFontHeightInPoints((short) 11);
            headStyle.setWriteFont(headFont);
            // 背景颜色
            headStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
            headStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
            //  ---------- 内容样式 ----------
            WriteCellStyle bodyStyle = new WriteCellStyle();
            StyleProperty styleProperty = new StyleProperty();
            // 字体样式
            WriteFont bodyFont = new WriteFont();
            bodyFont.setFontHeightInPoints((short) 10);
            bodyStyle.setWriteFont(bodyFont);
            // 设置边框
            // bodyStyle.setBorderTop(BorderStyle.DOUBLE);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            // 创建策略
            HorizontalCellStyleStrategy dataTableStrategy = new HorizontalCellStyleStrategy(headStyle, bodyStyle);

            /**
             * (4)、统一设置行高
             */
            // 设置表头行高【最上面的标题】  参数1：表头行高为0【不需要表头】    参数2：内容行高为28
            SimpleRowHeightStyleStrategy rowHeightStrategy1 = new SimpleRowHeightStyleStrategy((short) 0, (short) 28);
            // 设置表头备注栏行高【截止日期、板块内容】
            SimpleRowHeightStyleStrategy rowHeightStrategy2 = new SimpleRowHeightStyleStrategy((short) 0, (short) 25);
            // 设置数据表格的行高   null表示使用原来的行高
            SimpleRowHeightStyleStrategy rowHeightStrategy3 = new SimpleRowHeightStyleStrategy(null, (short) 18);

            /**(5)、生成页面中的3个表格
             *  0 , 1 , 2 是表格的排序(从上往下)
             *  上面设置的样式，合并。。。都需要在这里关联对应的表格
             */
            // 生成表格1 ----页面中最上方的大标题
            WriteTable topicTable = EasyExcel.writerTable(0).registerWriteHandler(rowHeightStrategy1).registerWriteHandler(mergeStrategy).registerWriteHandler(horizontalCellStyleStrategy).needHead(false).build();
            // 生成表格2 ----表头备注栏【截止日期、板块内容】
            WriteTable secondTable = EasyExcel.writerTable(1).registerWriteHandler(rowHeightStrategy2).registerWriteHandler(mergeStrategy2).registerWriteHandler(horizontalCellStyleStrategy2).needHead(false).build();
            // 生成表格3 ----user表格
            WriteTable dataTable = EasyExcel.writerTable(2).registerWriteHandler(new CustomRowHeightStyleStrategy(5)).registerWriteHandler(dataTableStrategy).head(head).needHead(true).build();
            /**(6)、把数据填充至各个表格 */
            build.write(topicData, sheet, topicTable);
            build.write(secondData, sheet, secondTable);
            build.write(data, sheet, dataTable);
            /**(7) 生成文件 */
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + "测试Excel" + ".xlsx");
        } catch (Exception e) {
            e.printStackTrace();
            /**
             * 前后端处理下载失败异常
             * 该部分必须与【流关闭操作】处于同一 【try-catch-finally】中，否则无法返回错误信息给前端
             */
            response.reset();
            response.setHeader("content-type", "text/html;charset=utf-8");
            try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                // 组装JSON
                Map<String, Object> map = new HashedMap<>();
                map.put("retCode", "9999");
                map.put("retMsg", "Excel下载失败：" + e.getMessage());
                String json = new ObjectMapper().writeValueAsString(map);
                if (!ObjectUtils.isEmpty(writer) && !ObjectUtils.isEmpty(json)) {
                    //返回错误信息给前端
                    writer.write(json);
                    writer.flush();
                }
            } catch (Exception e2) {
                throw new ClassCastException("response.getOutputStream()语句异常");
            }
        } finally {
            //关闭所有的流

            build.finish();

            if (!ObjectUtils.isEmpty(out)) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 不是填充Excel，而是直接往Excel中写
     * 需要从users数据集中，获取到value的部分，不需要key
     *
     * @return
     */
    private List<List<Object>> getData() {
        List<Dish> dishes = getDishes();
        List<List<Object>> data = new ArrayList<>();
        for (Dish dish : dishes) {
            // 把dish转为map
            String s = JSONObject.toJSONString(dish);
            Map jsonMap = JSONObject.parseObject(s, LinkedHashMap.class, Feature.OrderedField);
            // 获取map中的value，组装成list
            Collection values = jsonMap.values();
            List<Object> list = new ArrayList<>(values);
            data.add(list);
        }
        return data;
    }

    private List<Dish> getDishes() {
        List<Dish> list = this.list();
        return list;
//        List<UsersDTO> users = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            UsersDTO usersDTO = new UsersDTO();
//            usersDTO.setUserId("1000" + i);
//            usersDTO.setUserName("第" + i + "个用户");
//            usersDTO.setUserAge(18 + i + "");
//            users.add(usersDTO);
//        }
//        return users;
    }
}
