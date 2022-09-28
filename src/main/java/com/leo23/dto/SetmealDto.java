package com.leo23.dto;

import com.leo23.entity.Setmeal;
import com.leo23.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}
