package com.swp493.ivb.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ReactController implements ErrorController{
    private static final String PATH = "/error";

    @GetMapping(value="/")
    public ModelAndView getMethodName() {
        return new ModelAndView("index.html");
    }

    @RequestMapping(value = PATH)
    public ModelAndView error() {
        return new ModelAndView("index.html");
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}