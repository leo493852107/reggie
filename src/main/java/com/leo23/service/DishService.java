package com.leo23.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo23.dto.DishDto;
import com.leo23.entity.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品口味 dish, dish_flavor
    public void saveWithFlavor(DishDto dishDto);
}
