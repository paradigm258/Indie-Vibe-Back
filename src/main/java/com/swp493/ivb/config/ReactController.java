package com.swp493.ivb.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ReactController {
    @GetMapping(value="/")
    public ModelAndView getMethodName() {
        return new ModelAndView("index.html");
    }
}