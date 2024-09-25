package com.hwang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwang.reggie.entity.ShoppingCart;
import com.hwang.reggie.mapper.ShoppingCartMapper;
import com.hwang.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
