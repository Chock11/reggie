package com.hwang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwang.reggie.common.CustomException;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

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

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

        //但是有一个问题还要判断是否其售卖状态在起售,自己想复杂了，以为用的不是框架要单独遍历呢，这样将会增加难度。
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.in(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);

        if (count>0){
            throw new CustomException("删除的有包含在售卖的套餐");
        }

        //如果不包含
        this.removeByIds(ids);

        //删除玩Setmeal之后还要删除SetmealDish
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SetmealDish::getDishId,ids);

        setmealDishService.remove(wrapper);
    }

    @Override
    public void updateStatusByDish1(List<Long> ids) {
        for (Long id: ids){
            Setmeal byId = setmealService.getById(id);
            Long createUser = byId.getCreateUser();

                byId.setStatus(1);
                byId.setUpdateTime(LocalDateTime.now());
                byId.setUpdateUser(createUser);
            setmealService.saveOrUpdate(byId);
        }
    }

    @Override
    public void updateStatusByDish0(List<Long> ids) {
        for (Long id: ids){
            Setmeal byId = setmealService.getById(id);
            Long createUser = byId.getCreateUser();
            byId.setStatus(0);
            byId.setUpdateTime(LocalDateTime.now());
            byId.setUpdateUser(createUser);
            setmealService.saveOrUpdate(byId);
        }
    }
}
