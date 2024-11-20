package com.kostenko.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class IndexController {
    @RequestMapping("")
    fun redirectToFiles() = "redirect:/files"
}