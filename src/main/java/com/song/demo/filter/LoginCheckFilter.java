package com.song.demo.filter;

import com.alibaba.fastjson.JSON;
import com.song.demo.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
//这里显示的是拦截所有的请求
@WebFilter(filterName = "loginCheckFilter" ,urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //设置路径匹配器
    public static final AntPathMatcher PATH_MATCHER= new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        log.info("拦截到的请求是：{}",request.getRequestURI());
        //设置放行路径
        String[] urls = new String[] {
//            "/","/employee/login",
//                "/employee/logout",
//                "/backend/api/**",
//                "/backend/images/**",
//                "/backend/js/**",
//                "/backend/plugins/**",
//                "/backend/styles/**",
//                "/backend/favicon.ico"
//                ,"/front/api/**",
//                "/front/images/**",
//                "/front/js/**",
//                "/front/styles/**",
//                "/front/fonts/**"
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        //1、获取本次请求的URI
        String  requestURL = request.getRequestURI();
        //2、判断本次请求是否需要处理
        if(checkRequest(urls,requestURL)){
            log.info("本次请求不需要处理{}",request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }
        //3、如果不需要处理，则直接放行

        //4、判断登录状态，如果已登录，则直接放行
        HttpSession session = request.getSession();
        Object employee = session.getAttribute("employee");
        if(employee!=null){
            log.info("已经登录{}",request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }

        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    public boolean checkRequest(String [] urls,String url){
        for (String s : urls) {
            boolean match = PATH_MATCHER.match(s, url);
            if(match) return true;
        }
        return false;
    }
}
