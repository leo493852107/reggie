package com.leo23.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo23.common.CustomException;
import com.leo23.entity.Category;
import com.leo23.entity.Dish;
import com.leo23.entity.Setmeal;
import com.leo23.mapper.CategoryMapper;
import com.leo23.service.CategoryService;
import com.leo23.service.DishService;
import com.leo23.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 查询当前分类是否关联菜品，如果已关联抛出业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        long count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            throw new CustomException("当前分类关联菜品，不能删除");
        }

        // 查询当前分类是否关联套餐，如果已关联抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        long count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            throw new CustomException("当前分类关联套餐，不能删除");
        }

        // 正常删除分类
        super.removeById(id);
    }
}
