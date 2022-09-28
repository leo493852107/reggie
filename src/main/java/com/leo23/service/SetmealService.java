package com.leo23.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo23.dto.SetmealDto;
import com.leo23.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    // 新增套餐，同时保存套餐与菜品的关系
    public void saveWithDish(SetmealDto setmealDto);
}
