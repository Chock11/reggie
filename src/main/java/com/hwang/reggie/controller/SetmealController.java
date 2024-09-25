package com.hwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwang.reggie.common.CustomException;
import com.hwang.reggie.common.R;
import com.hwang.reggie.dto.SetmealDto;
import com.hwang.reggie.entity.Category;
import com.hwang.reggie.entity.Setmeal;
import com.hwang.reggie.entity.SetmealDish;
import com.hwang.reggie.service.CategoryService;
import com.hwang.reggie.service.SetmealDishService;
import com.hwang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Resource
    private SetmealService setmealService;

    @Resource
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /*
    * 新增套餐
    * */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){

        if(setmealDto!=null){
            setmealService.saveWithDish(setmealDto);
        }else {
            return R.error("不能添加空套餐，请重新添加！");
        }

        return R.success("保存菜品成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页查询
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

        //构造条件过滤器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        wrapper.like(name!=null,Setmeal::getName,name);

        //添加排序条件
        wrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行查询条件
        setmealService.page(setmealPage,wrapper);

        //对象拷贝
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(collect);

        return R.success(setmealDtoPage);

    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        if (ids!=null){
            setmealService.removeWithDish(ids);
        }else {
            R.error("请选择要删除的套餐");
        }

        return  R.success("套餐删除成功");
    }

    /*
    * 这样写有一个大错误就是不能批量起售和停售
    * */
//    @PostMapping("/status/{status}")
//    public R<String> updateStatus(@RequestParam List<Long> ids){
//        if (ids!=null){
//            setmealService.updateStatusByDish(ids);
//        }else {
//            R.error("请选择要修改的的套餐");
//        }
//        return R.success("修改套餐成功！");
//    }

    @PostMapping("/status/1")
    public R<String> updateStatus1(@RequestParam List<Long> ids){
        if (ids!=null){
            setmealService.updateStatusByDish1(ids);
        }else {
            R.error("请选择要修改的的套餐");
        }
        return R.success("修改套餐成功！");
    }

    @PostMapping("/status/0")
    public R<String> updateStatus0(@RequestParam List<Long> ids){
        if (ids!=null){
            setmealService.updateStatusByDish0(ids);
        }else {
            R.error("请选择要修改的的套餐");
        }
        return R.success("修改套餐成功！");
    }

    @GetMapping("/{id}")
    public R<Setmeal> delete(@RequestParam Long id){

        Setmeal setmeal = setmealService.getById(id);

        return R.success(setmeal);
    }

    /*
    * 根据条件查询套餐数据
    * */
    @GetMapping("/list")
    public R<List<Setmeal>> list( Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }


}
