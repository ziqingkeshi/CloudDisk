package com.yht.dao;

import java.util.List;
import java.util.Map;

public interface LoginDao {
    /**
     * 用户登录方法
     * @param username
     * @param password
     * @return
     */
    List<Map> login(String username, String password);
}
