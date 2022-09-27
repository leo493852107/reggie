package com.leo23.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo23.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
