package com.cyc.schoolcanteen.filter;

import com.alibaba.fastjson.JSON;
import com.cyc.schoolcanteen.common.BaseContext;
import com.cyc.schoolcanteen.dto.Result;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-15 14:34
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String[] urls = {
                "employee/login",
                "employee/logout",
                "static/pages/login/*",
                "static/js/**",
                "static/styles/**",
                "static/plugins/**",
                "static/api/**",
                "static/images/**",
                "common/*",
                "static/pages/upload.html",
                "front/pages/login.html",
                "user/*",
                "front/js/**",
                "front/styles/**",
                "front/plugins/**",
                "front/api/**",
                "front/images/**"
        };
        //1. 获取请求url
        String url = request.getRequestURL().toString().split("/",4)[3];
        //2. 是否匹配处理
        boolean isMatch = match(urls, url);
        if (isMatch) {
            filterChain.doFilter(request, response);
            return;
        }
        //3. 检查登陆状态
        if (request.getSession().getAttribute("userId") != null) {
            Long userId = (Long) request.getSession().getAttribute("userId");
            BaseContext.setId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        if(request.getSession().getAttribute("userPhone") != null){
            Long userPhone = (Long) request.getSession().getAttribute("userPhone");
            BaseContext.setId(userPhone);
            filterChain.doFilter(request,response);
            return;
        }
        //4. 返回未登录的结果
        response.getWriter().write(JSON.toJSONString(Result.error("NOT LOGIN")));
    }

    private boolean match(String[] urls, String url) {
        for (String str : urls) {
            boolean isMatch = PATH_MATCHER.match(str, url);
            if (isMatch) {
                return true;
            }
        }

        return false;
    }
}
