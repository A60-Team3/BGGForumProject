package com.example.bggforumproject.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class HomeMvcController {

    @GetMapping
    public String getHomePage(){
        return "redirect:/BGGForum/main";
    }

    @GetMapping("/BGGForum")
    public String redirectHomePage(){
        return "redirect:/BGGForum/main";
    }
}
