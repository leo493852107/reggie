package com.leo23.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leo23.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
