package com.leo23.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo23.dto.DishDto;
import com.leo23.entity.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品口味 dish, dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    // 更新菜品信息, 同时更新口味信息
    public void updateWithFlavor(DishDto dishDto);

    // 1.删除口味信息 2.删除菜品信息
    public void deleteWithFlavor(Long ids);
}
