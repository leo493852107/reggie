package com.leo23.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo23.entity.OrderDetail;
import com.leo23.mapper.OrderDetailMapper;
import com.leo23.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
