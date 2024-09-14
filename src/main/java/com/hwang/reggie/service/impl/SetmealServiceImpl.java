package com.hwang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwang.reggie.dto.SetmealDto;
import com.hwang.reggie.entity.Dish;
import com.hwang.reggie.entity.Setmeal;
import com.hwang.reggie.entity.SetmealDish;
import com.hwang.reggie.mapper.SetMealMapper;
import com.hwang.reggie.service.SetmealDishService;
import com.hwang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存到
        this.save(setmealDto);

        //找到
        Long setmealDtoId = setmealDto.getId();
        String setmealDtoId1 =setmealDto.getId().toString();

        //找到对应的菜品id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((items)->{
            items.setDishId(setmealDtoId);
            items.setSetmealId(setmealDtoId);
            return items;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
