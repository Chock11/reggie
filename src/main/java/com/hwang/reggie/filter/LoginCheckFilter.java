package com.hwang.reggie.filter;

import cn.hutool.core.text.AntPathMatcher;
import com.alibaba.fastjson.JSON;
import com.hwang.reggie.common.BaseContext;
import com.hwang.reggie.common.R;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request =(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        //获得本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到的URI：{}",requestURI);
        //添加一个数组保存需要放行的URI
        String[] uri =new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"

        };
        //判断本次请求是否需要处理

        boolean check = check(uri, requestURI);

        //如果不需要处理直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //如果已登录，则直接放行
        Object employee = request.getSession().getAttribute("employee");
        if (employee!=null){
            log.info("用户已登录！用户的id为:"+request.getSession().getAttribute("employee"));

            Long attribute = (Long)request.getSession().getAttribute("employee");

            BaseContext.setCurrentId(attribute);
            filterChain.doFilter(request,response);
            return;
        }


        //判断移动端的登陆状态，则直接放行
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已登录！用户的id为:"+request.getSession().getAttribute("user"));

            Long userId = (Long)request.getSession().getAttribute("user");

            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录，那么通过输出流的方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;



    }

    /*
    * 路径匹配，检查本次请求是否需要放行
    * */
    public  boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }

        return false;
    }


}
