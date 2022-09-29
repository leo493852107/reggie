package com.leo23.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leo23.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
