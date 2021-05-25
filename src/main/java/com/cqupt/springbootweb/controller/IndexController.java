package com.cqupt.springbootweb.controller;

import com.cqupt.springbootweb.bean.User;
import com.cqupt.springbootweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;


@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @GetMapping({"/", "/login"})
    public String loginPage(){
        return "login";
    }

    @PostMapping("/loginPost")
    public String redMainPage(User user, HttpSession session, Model model){
        User selectByUserNameAndPassword = userService.selectByUserNameAndPassword(user);
        if (selectByUserNameAndPassword != null) {
            session.setAttribute("login", selectByUserNameAndPassword);

            //  解决表单重复提交
            return "redirect:/main.html";
        }
        else {
            model.addAttribute("msg", "账号密码错误");
            return "login";
        }
    }

    @GetMapping("/registration.html")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationPost(String userName, String password){
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        user.setTotalCount(0);
        user.setInvalidCount(0);

        userService.insertUser(user);
        return "redirect:/login";
    }

    @GetMapping("/main.html")
    public String mainPage(HttpSession session, Model model){
        return "main";
    }

    @GetMapping("slider")
    public String slider() {
        return "slider";
    }
}
