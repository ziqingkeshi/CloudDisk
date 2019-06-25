package com.yht.controller;

import com.yht.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.yht.util.other.MD5.md5;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private HttpSession session;

    /**
     * 用户登录方法
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model){
        String username= request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username+"====="+password);
        if(username!="" && username!=null && password!="" && password!=null) {
            System.out.println("加密后的密码：" + md5(password));
            //去数据库验证用户名密码
            List<Map> maps = loginService.login(username, md5(password));
            if (maps!=null && !maps.isEmpty() ){
                //将用户id放入session中
                session.setAttribute("userid", maps.get(0).get("userid"));
                //用户名密码正确
                model.addAttribute("msg", "");
                return "redirect:/toIndex";
            }else {
                //用户名密码错误
                model.addAttribute("msg", "用户名或密码错误，请重试!");
                return "login/login";
            }
        }else {
            model.addAttribute("msg", "你这样是进不去的!");
            return "login/login";
        }
    }

    /**
     * 跳转主页面
     * @return
     */
    @RequestMapping("/toIndex")
    public String toIndex(){
        return "main/index";
    }
}
