package com.hwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwang.reggie.common.BaseContext;
import com.hwang.reggie.common.R;
import com.hwang.reggie.entity.Category;
import com.hwang.reggie.entity.Dish;
import com.hwang.reggie.entity.Setmeal;
import com.hwang.reggie.service.CategoryService;
import com.hwang.reggie.service.DishService;
import com.hwang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
      log.info("传过来的添加分类是: "+category);
//      category.setCreateTime(LocalDateTime.now());
//      category.setUpdateTime(LocalDateTime.now());
//      category.setCreateUser(BaseContext.getCurrentId());
//      category.setUpdateUser(BaseContext.getCurrentId());

      categoryService.save(category);
      return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        log.info("page= {},pageSize= {}",page,pageSize);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        //排序条件,根据sort字段排序
        queryWrapper.orderByDesc(Category::getSort);

        //执行查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);


    }
    /*
    * 根据id删除分类
    * 这个里面有一个坑的东西在于前端传过来的id是ids而不是id。
    * */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("要删除的分类Id是:"+ids);
        //判断其分类下面是否有套餐存在
        categoryService.remove(ids);

        return R.success("分类信息删除成功");
    }

    /*
    * 根绝id修改餐品分类
    * */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("要修改的内容是什么:"+category.toString());

        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryService.updateById(category);

        return R.success("修改餐品分类成功");
    }

    /*
    * 根据条件菜品管理获取菜品分类
    * */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper =new LambdaQueryWrapper();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }



}
