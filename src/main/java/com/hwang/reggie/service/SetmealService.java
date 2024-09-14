package com.hwang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwang.reggie.dto.SetmealDto;
import com.hwang.reggie.entity.Dish;
import com.hwang.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
}
