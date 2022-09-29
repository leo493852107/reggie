package com.leo23.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo23.entity.Orders;

public interface OrderService extends IService<Orders> {

    // 用户下单
    void submit(Orders orders);
}
