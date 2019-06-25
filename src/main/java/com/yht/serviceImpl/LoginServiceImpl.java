package com.yht.serviceImpl;

import com.yht.dao.LoginDao;
import com.yht.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginDao loginDao;

    @Override
    public List<Map> login(String username, String password) {
        return loginDao.login(username, password);
    }
}
