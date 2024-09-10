package com.hwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwang.reggie.common.R;
import com.hwang.reggie.dto.DishDto;
import com.hwang.reggie.entity.Category;
import com.hwang.reggie.entity.Dish;
import com.hwang.reggie.service.CategoryService;
import com.hwang.reggie.service.DishFlavorService;
import com.hwang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    /*
    添加菜品*/
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("接收到的数据是:"+ dishDto.toString());
        if(dishDto!=null){
            dishService.saveWithFlavor(dishDto);
        }

        return R.success("成功添加菜品");
    }

    /*
    * 菜品的分页查询
    * */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页查询
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper();

        //添加过滤条件
        wrapper.like(name!=null,Dish::getName, name);

        //添加排序条件
        wrapper.orderByDesc(Dish::getUpdateTime);

        //执行查询
        dishService.page(pageInfo,wrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        //这一点有点难理解，但是还可以。
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto =new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    @GetMapping("/page")
    public R<DishDto> update(Long id){
        //根据id找到菜品
        Dish dish = dishService.getById(id);
        //根据获得到的对象得到对应的categoryId
        Long categoryId = dish.getCategoryId();
        //通过categoryId找到对应的
        Category category = categoryService.getById(categoryId);

        return R.success();
    }

}
