package com.hwang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwang.reggie.entity.SetmealDish;
import com.hwang.reggie.mapper.SetmealDishMapper;
import com.hwang.reggie.service.SetmealDishService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
