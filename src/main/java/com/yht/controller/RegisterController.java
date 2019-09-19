package com.yht.controller;


import com.yht.entity.User;
import com.yht.service.RegisterService;
import com.yht.util.ShowApiRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Controller
@RequestMapping("/user")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    /**
     * 注册方法
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping("/register")
    public Map<String, String> register(User user){
        int i = registerService.register(user);
        Map<String,String> tempMap = new HashMap<>();
        if (i>0){
            tempMap.put("msg","success");
        }else {
            tempMap.put("msg","success");
        }
        return tempMap;
    }

    /**
     * 用户名验重
     * @param username
     * @return
     */
    @RequestMapping("/checkUserName")
    @ResponseBody
    public Map<String,String> checkUserName(@RequestParam String username) {
        System.out.println(username);
        String s = registerService.checkRepeat(username);
        Map<String,String> nameMap = new HashMap<>();
        if(s!=null){
            nameMap.put("msg","fail");
        }else{
            nameMap.put("msg","success");
        }
        return nameMap;
    }

    /**
     * 手机号验重
     * @param telephone
     * @return
     */
    @RequestMapping("/checkPhone")
    @ResponseBody
    public Map<String,String> checkPhone(@RequestParam String telephone) {
        String s = registerService.checkRepeatPhone(telephone);
        Map<String,String> phoneMap = new HashMap<>();
        if(s!=null){
            phoneMap.put("msg","fail");
        }else{
            phoneMap.put("msg","success");
        }
        return phoneMap;
    }


    @ResponseBody
    @RequestMapping("/testEncoder")
    public String testEncoder() throws Exception {
        String encode = URLEncoder.encode("=", "utf-8");
        System.out.println("编码：" + encode);
        String decode = URLDecoder.decode(encode, "utf-8");// UTF-8解码
        System.out.println("解码：" + decode);
        String temp = encode + "-----" + decode;
        return temp;
    }

    /**
     * 跳转登录界面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login/login";
    }

    /**
     * 跳转注册界面
     * @return
     */
    @RequestMapping("/toRegister")
    public String toRegister(){
        return "register/register";
    }

    /**
     * 根据地名获取经纬度
     * @param path
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/testCity")
    public String test(String path[]) throws Exception {
        String myappid = "myappid";
        String mySecret = "mySecret";
        URL u = new URL("http://route.showapi.com/238-1?showapi_appid=" + myappid + "&address=郑州大学&city=郑州&showapi_sign=" + mySecret);
        InputStream in = u.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte buf[] = new byte[1024];
            int read = 0;
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        byte b[] = out.toByteArray();
        String s = new String(b, "utf-8");
        System.out.println(s);
        return s;
    }


    /***
     * 短信模板提交测试
     */
    @RequestMapping("/getMessage")
    @ResponseBody
    public String get() {
        String res = (new ShowApiRequest("http://route.showapi.com/28-2", "myappid", "mySecret"))
                .addTextPara("content", "尊敬的用户您好!您本次的验证码是:[code],有效时间为3分钟!")
                .addTextPara("title", "天天云盘")
                .post();
        return res;
    }

    /**
     * 修改短信模板
     *
     * @return
     */
    @RequestMapping("/alterMould")
    @ResponseBody
    public String alterModule() {
        String res = (new ShowApiRequest("http://route.showapi.com/28-10", "myappid", "mySecret"))
                .addTextPara("content", "尊敬的用户您好!您本次的验证码是:[code],有效时间为3分钟!")
                .addTextPara("title", "天天云盘")
                .addTextPara("tNum", "T170317003567")
                .addTextPara("notiPhone", "电话")
                .post();
        return res;
    }

    /***
     * 短信模板是否通过测试
     */
    @RequestMapping("/getMould")
    @ResponseBody
    public String modelOk() {
        String res = (new ShowApiRequest("http://route.showapi.com/28-3", "myappid", "mySecret"))
                .addTextPara("nomalTemplate", "2")
                .post();
        return res;
    }

    /**
     * 发送短信
     *
     * @return
     */
    @RequestMapping("/sendMessage")
    @ResponseBody
    public Map sendMessage(@RequestParam String phone, HttpSession session) {
        int randomNum = (int) ((Math.random() * 9 + 1) * 100000);
        Map tempMap = new HashMap();
        String res = (new ShowApiRequest("http://route.showapi.com/28-1", "myappid", "mySecret"))
                .addTextPara("mobile", phone)
                .addTextPara("content", "{\"name\":\"严浩天\",\"code\":\"" + randomNum + "\",\"minute\":\"3\"}")
                .addTextPara("tNum", "T170317004205")
                .post();
        String attrName = "vcode";
        session.setAttribute("vcode", randomNum);
//        session.setAttribute("vcode","123456");
        this.removeAttrbute(session, attrName);
        tempMap.put("vcode", randomNum);
//        tempMap.put("vcode","123456");
        tempMap.put("sessionId", session.getId());
        return tempMap;
    }

    /**
     * 设置5分钟后删除session中的验证码
     *
     * @param session
     * @param attrName
     */
    private void removeAttrbute(final HttpSession session, final String attrName) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 删除session中存的验证码
                session.removeAttribute(attrName);
                timer.cancel();
            }
        }, 18 * 10 * 1000); //1分钟 = 6 * 10 * 1000ms
    }

    /**
     * 判断验证码是否超时
     *
     * @param session
     * @return
     */
    @RequestMapping("/hasVcode")
    @ResponseBody
    public Map hasVcode(HttpSession session) {
        Object vcode = session.getAttribute("vcode");
        Map tempMap = new HashMap();
        if (vcode != null) {
            tempMap.put("msg", "success");
        } else {
            tempMap.put("msg", "fail");
        }
        return tempMap;
    }


    /**
     * 删除短信模板
     *
     * @return
     */
    @RequestMapping("/deleteMould")
    @ResponseBody
    public String deleteMould() {
        String res = (new ShowApiRequest("http://route.showapi.com/28-9", "myappid", "mySecret"))
                .addTextPara("tNum", "T170317003566")
                .post();
        return res;
    }
}
