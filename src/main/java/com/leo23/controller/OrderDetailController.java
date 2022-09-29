package com.leo23.controller;

import com.leo23.service.OrderDetailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Resource
    private OrderDetailService orderDetailService;

}
