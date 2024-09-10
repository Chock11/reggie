package com.hwang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwang.reggie.common.CustomException;
import com.hwang.reggie.common.R;
import com.hwang.reggie.entity.Category;
import com.hwang.reggie.entity.Dish;
import com.hwang.reggie.entity.Setmeal;
import com.hwang.reggie.mapper.CategoryMapper;
import com.hwang.reggie.service.CategoryService;
import com.hwang.reggie.service.DishService;
import com.hwang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    SetmealService setmealService;

    /*
    * 根绝id删除，删除之前先判断
    * */
    @Override
    public void remove(Long ids) {
        log.info("要删除的分类Id是:"+ids);

        //查询当前菜品是否关联了菜品，如果已经关联，抛出一个异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper1 = dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count1 = dishService.count(dishLambdaQueryWrapper1);
        if (count1>0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前菜品是否关联了套餐，如果已经关联，抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper1 = setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper1);
        if (count2>0){
            //抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除分类

        categoryService.removeById(ids);




    }
}
