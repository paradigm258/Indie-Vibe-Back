package com.swp493.ivb.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ReactController implements ErrorController{
    private static final String PATH = "/error";

    @GetMapping(value="/")
    public ModelAndView getMethodName() {
        return new ModelAndView("index.html");
    }

    @RequestMapping(value = PATH)
    public String error(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String message = (String) request.getAttribute("javax.servlet.error.message");
        throw new ResponseStatusException(HttpStatus.valueOf(status),message);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}