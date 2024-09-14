package com.hwang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwang.reggie.dto.DishDto;
import com.hwang.reggie.entity.Dish;
import com.hwang.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;


public interface DishService extends IService<Dish> {
    /*
    * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表
    * */
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
