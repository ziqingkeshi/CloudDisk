package com.yht.serviceImpl;

import com.yht.dao.RegisterDao;
import com.yht.entity.User;
import com.yht.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.yht.util.other.MD5.md5;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private RegisterDao registerDao;

    @Override
    public int register(User user) {
        //对密码进行md5加密
        user.setPassword(md5(user.getPassword()));
        return registerDao.register(user);
    }

    @Override
    public String checkRepeat(String userName) {
        return registerDao.checkRepeat(userName);
    }

    @Override
    public String checkRepeatPhone(String userPhone) {
        return registerDao.checkRepeatPhone(userPhone);
    }
}
