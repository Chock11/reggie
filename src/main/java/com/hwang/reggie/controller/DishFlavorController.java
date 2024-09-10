package com.hwang.reggie.controller;

import com.hwang.reggie.common.R;
import com.hwang.reggie.dto.DishDto;
import com.hwang.reggie.service.DishFlavorService;
import com.hwang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
//@RequestMapping("/dish")
public class DishFlavorController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /*
    添加菜品*/
    @PostMapping
    public R<String> save(@RequestBody  DishDto dishDto){
        log.info("接收到的数据是:"+dishDto.toString());
        return null;
    }
}
