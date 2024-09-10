package com.hwang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwang.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long ids);

}
