package com.snms.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DemoInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String theMethod = request.getMethod();

        if (HttpMethod.GET.matches(theMethod) || HttpMethod.POST.matches(theMethod)) {
            // GET, POST methods are allowed
            return true;
        }
        else {
            // everything else is not allowed
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value());
            return false;
        }
    }
}