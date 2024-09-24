package com.hwang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwang.reggie.dto.SetmealDto;
import com.hwang.reggie.entity.Dish;
import com.hwang.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    /*
    * 删除套餐，同时需要删除套餐和菜品的关联数据
    * */
    public void removeWithDish(List<Long> ids);

//    public void updateStatusByDish(List<Long> ids);
    public void updateStatusByDish1(List<Long> ids);
    public void updateStatusByDish0(List<Long> ids);



}
