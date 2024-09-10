package com.hwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwang.reggie.common.R;
import com.hwang.reggie.entity.Employee;
import com.hwang.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /*
    * 员工登录
    * */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //获取密码
        String password = employee.getPassword();
        //使用DigestUtils工具类来进行
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //如果没有查到则返回登陆失败结果
        if(emp == null){
            return R.error("用户名或密码错误，登陆失败！");
        }

        //判断登录密码是否正确
        if(!emp.getPassword().equals(password)){
            return R.error("用户名或密码错误，登陆失败！");
        }

        if(emp.getStatus()==0){
            return R.error("该账户已被禁用！登陆失败！");
        }

        //登录成功之后将员工的id存入到session当中
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    /*
    * 员工推出
    * */
    @PostMapping("/logout")
    public  R<String> logout(HttpServletRequest request){
        //session中清楚当前登陆的员工id
        request.getSession().removeAttribute("employee");
        return R.success("推出成功");
    }

    /*
    * 新增员工
    * */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工:{}",employee.toString());

//        Long id =Thread.currentThread().getId();
//        log.info("当前的线程Id: {}",id);

        //设置初始密码123456，进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //获得当前登录用户的id
//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /*
    * 员工信息分页查询
    * */
    @GetMapping("/page")
    public R<Page>  page(int page, int pageSize,String name){
        log.info("page= {},pageSize= {},name= {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper =new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);

    }

    /*
    * 根据id修改用户状态
    * */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

        log.info(employee.toString());

        Long employee1 = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(employee1);
        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);

        return R.success("修改成功");
    }


    /*
    * 根据id查询员工信息,存在一个问题修改的时间格式前端传过来的是2000-00-00格式而数据库中村的是LocalDateTime格式不能够保存
    * */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("未找到对应的用户");
    }
}
