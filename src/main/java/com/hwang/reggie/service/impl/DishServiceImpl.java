package com.hwang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwang.reggie.dto.DishDto;
import com.hwang.reggie.entity.Dish;
import com.hwang.reggie.entity.DishFlavor;
import com.hwang.reggie.mapper.DishMapper;
import com.hwang.reggie.service.DishFlavorService;
import com.hwang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    public DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品到菜品表
        this.save(dishDto);

        //获取菜品id
        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        //使用流进行遍历然后设置flavors当中的dishId，为什么设置因为传过来的复制语句当中没有ID这一项。
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存口味数据到口味表
        dishFlavorService.saveBatch(flavors);
    }

}
