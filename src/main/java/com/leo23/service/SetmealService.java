package com.leo23.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo23.dto.SetmealDto;
import com.leo23.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    // 新增套餐，同时保存套餐与菜品的关系
    public void saveWithDish(SetmealDto setmealDto);

    // 删除套餐，同时删除套餐与菜品的关联数据
    public void removeWithDish(List<Long> ids);
}
