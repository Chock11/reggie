package com.hwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hwang.reggie.common.BaseContext;
import com.hwang.reggie.common.R;
import com.hwang.reggie.entity.ShoppingCart;
import com.hwang.reggie.entity.User;
import com.hwang.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.LambdaConversionException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shopping")
public class ShoppingCartController {

    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //设置用户id，阿里登陆当前用户内容
        Long currentId = BaseContext.getCurrentId();

        shoppingCart.setId(currentId);

        //设置完id之后呢要进行菜品的判断
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        if (shoppingCart.getDishId()!=null){
            wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前菜品
        ShoppingCart cartServiceOne = shoppingCartService.getOne(wrapper);

        //如果已经存在菜品，那么就把数量加一
        if(cartServiceOne!=null){
            Integer cartNumber = shoppingCart.getNumber();

            cartServiceOne.setNumber(cartNumber+1);

            shoppingCartService.updateById(cartServiceOne);


        }else {

            //如果不存在，则添加到购物车，数量默认就是1
            shoppingCart.setNumber(1);
            cartServiceOne.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    /*
    * 查看购物车
    * */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCarts);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("成功删除购物车");
    }


}
