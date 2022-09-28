package com.leo23.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo23.dto.DishDto;
import com.leo23.entity.Dish;
import com.leo23.entity.DishFlavor;
import com.leo23.mapper.DishMapper;
import com.leo23.service.DishFlavorService;
import com.leo23.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
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
        // 删除 dish
        super.removeById(ids);
    }
}
