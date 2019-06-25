package com.yht.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private HttpSession session;

    /**
     * 用户注销登录方法
     * @return
     */
    @RequestMapping("/userLogout")
    @ResponseBody
    public Map<String,String> userLogout(){
        //回参map
        Map<String,String> resultMap = new HashMap<>();
        //移除session中的user信息
        session.removeAttribute("userid");
        Object userid = session.getAttribute("userid");
        if(userid==null){
            resultMap.put("status","200");
        }else{
            resultMap.put("status","200");
        }
        return resultMap;
    }

//    @RequestMapping("/test")
//    public String test(){
//        return "/test";
//    }
//
//    /**
//     * 文件夹操作测试
//     * @param args
//     */
//    public static void main(String[] args){
//
//        getFiles("/Library/Tomcat");
//    }
//
//    /**
//     * 文件夹操作方法
//     * @param path
//     */
//    public static void getFiles(String path){
//        /**
//         * 递归获取某路径下的所有文件，文件夹，并输出
//         */
//        File file = new File(path);
//        // 如果这个路径是文件夹
//        if (file.isDirectory()) {
//            // 获取路径下的所有文件
//            File[] files = file.listFiles();
//            for (int i = 0; i < files.length; i++) {
//                // 如果还是文件夹 递归获取里面的文件 文件夹
//                if (files[i].isDirectory()) {
//                    System.out.println("目录：" + files[i].getPath());
//                    getFiles(files[i].getPath());
//                } else {
//                    System.out.println("文件：" + files[i].getPath());
//                }
//
//            }
//        } else {
//            System.out.println("文件：" + file.getPath());
//        }
//    }

}
