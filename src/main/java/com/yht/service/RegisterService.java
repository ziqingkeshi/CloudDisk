package com.yht.service;

import com.yht.entity.User;

public interface RegisterService {
    /**
     * 用户注册方法
     * @param user
     * @return
     */
    int register(User user);

    /**
     * 注册的用户名查重
     * @param userName
     * @return
     */
    String checkRepeat(String userName);

    /**
     * 注册的手机号查重
     * @param userPhone
     * @return
     */
    String checkRepeatPhone(String userPhone);
}
