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


    /*
    * 根据id查询菜品信息和对应的口味信息
    * */
    @GetMapping("/{id}")
    public R<DishDto> update(@PathVariable  Long id){

        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);

        return R.success(byIdWithFlavor);
    }


    /*
    修改菜品*/
    @PutMapping
    public R<String> updated(@RequestBody DishDto dishDto){
        log.info("接收到的数据是:"+ dishDto.toString());
        //更新操作不像增加操作，这个修改要改两张表
        if(dishDto!=null){
            dishService.updateWithFlavor(dishDto);
        }

        return R.success("成功添加菜品");
    }

    /*
    *根据条件查询对应的菜品数据
    * */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        queryWrapper.eq(Dish::getStatus,1);
        //这一种的查询返回出来的是list集合类型的
        List<Dish> dishes = dishService.list(queryWrapper);
        return R.success(dishes);
    }

}
