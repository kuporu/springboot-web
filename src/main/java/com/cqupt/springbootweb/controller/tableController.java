package com.cqupt.springbootweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.springbootweb.bean.User;
import com.cqupt.springbootweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.websocket.server.PathParam;
import java.util.Arrays;
import java.util.List;

@Controller
public class tableController {
    @Autowired
    UserService userService;

    @GetMapping("/basic_table")
    public String basic_table(){
        return "table/basic_table";
    }

    @GetMapping("/dynamic_table/delete/{id}")
    //    @PathParam("id") Long id
    public String delete(@PathVariable("id") Long id,
                         @RequestParam("pn") int pn,
                         RedirectAttributes redirectAttributes){
        userService.removeById(id);
        //  放置重定向参数值
        redirectAttributes.addAttribute("pn",pn);
        return "redirect:/dynamic_table";
    }

    @GetMapping("/dynamic_table")
    public String dynamic_table(@RequestParam(value = "pn",defaultValue = "1") int pn, Model model)
    {
        // 分页练习
        Page<User> userPage = new Page<>(pn, 2);
        IPage<User> page = userService.page(userPage, null);
        model.addAttribute("page", page);
        return "table/dynamic_table";
    }

    @GetMapping("/responsive_table")
    public String responsive_table(){
        return "table/responsive_table";
    }

    @GetMapping("editable_table")
    public String editable_table(){
        return "table/editable_table";
    }
}
