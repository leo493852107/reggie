package com.leo23.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo23.common.CustomException;
import com.leo23.common.MyUtils;
import com.leo23.dto.SetmealDto;
import com.leo23.entity.Setmeal;
import com.leo23.entity.SetmealDish;
import com.leo23.mapper.SetMealMapper;
import com.leo23.service.SetmealDishService;
import com.leo23.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetmealService {
    @Value("${reggie.path}")
    private String filePath;
    @Resource
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // insert setmeal 套餐的基本信息
        this.save(setmealDto);

        // insert setmeal_dish 套餐与菜品的关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 先查询套餐状态，是否可售
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, 1);

        long count = this.count(wrapper);
        if (count > 0) {
            // 不能删除，抛出业务异常
            throw new CustomException("套餐售卖中，不能删除");
        }
        // 可以删除
        // 1.删除 图片文件
        MyUtils utils = new MyUtils();
        for (Long id : ids) {
            utils.deleteFile(filePath + this.getById(id).getImage());
        }
        // 2. 删除 Setmeal
        this.removeByIds(ids);
        // 3. 删除 Setmeal_dish
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(dishLambdaQueryWrapper);

    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {

    }
}
