package com.yht.serviceImpl;

import com.yht.dao.UserFilesDao;
import com.yht.service.UserFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:UserFilesServiceImpl
 * Description:
 * Author:严浩天
 * CreateTime:2019-04-07 23:52
 */
@Service
public class UserFilesServiceImpl implements UserFilesService {

    @Autowired
    private UserFilesDao userFilesDao;

    @Autowired
    private HttpSession session;

    @Override
    public List<Map> userFileList(Map map) {
        List<Map> maps = userFilesDao.userFileList(map);
        return maps;
    }

    @Override
    public int userFileCount(Map map) {
        int i = userFilesDao.userFileCount(map);
        return i;
    }

    @Override
    public Map<String, String> userNewFolder(Map map) {
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //只查找belongId属于当前登录用户userid的文件
        map.put("belongId", userId);
        map.put("fileType", 1); //fileType=1代表文件类型是文件夹
        map.put("fileSize", 1); //新建文件夹默认大小是0
        //调用方法查看文件名是否重复
        List<Map> fileNameRepeat = userFilesDao.isFileNameRepeat(map);
        //创建一个tempMap像Controller层传值
        Map<String,String> tempMap = new HashMap<>();
        //如果fileNamRepeat不为空 说明文件名重复
        if(fileNameRepeat!=null && !fileNameRepeat.isEmpty()){
            //为yes时代表重复
            tempMap.put("isRepeat","yes");
        }else{
            //为no时代表不重复
            tempMap.put("isRepeat","no");
            //调用方法将新建文件夹信息保存至数据库
            int i = userFilesDao.userNewFolder(map);
            if(i>0){
                //i>0说明已经保存到数据库中
                tempMap.put("status","200");
            }else{
                tempMap.put("status","500");
            }
        }
        return tempMap;
    }

    @Override
    public int dleteFiles(int belongId, String[] deleteFilesIds) {
        //调用删除方法将用户删除的文件放入回收站
        return userFilesDao.deleteFiles(belongId,deleteFilesIds);
    }

    @Override
    public int userDeleteFiles(int belongId, String[] deleteFilesIds) {
        //调用删除文件方法批量删除文件
        return userFilesDao.userDeleteFiles(belongId,deleteFilesIds);
    }

    @Override
    public List<Map> isFileNameRepeat(Map map) {
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //只查找belongId属于当前登录用户userid的文件
        map.put("belongId", userId);
        return userFilesDao.isFileNameRepeat(map);
    }

    @Override
    public Map<String, String> userUpdateFileName(Map map) {
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //只查找belongId属于当前登录用户userid的文件
        map.put("belongId", userId);
        //调用方法查看文件名是否重复
        List<Map> fileNameRepeat = userFilesDao.isFileNameRepeat(map);
        //创建一个tempMap像Controller层传值
        Map<String,String> tempMap = new HashMap<>();
        //如果fileNamRepeat不为空 说明文件名重复
        if(fileNameRepeat!=null && !fileNameRepeat.isEmpty()){
            //为yes时代表重复
            tempMap.put("isRepeat","yes");
        }else {
            //为no时代表不重复
            tempMap.put("isRepeat", "no");
            int i = userFilesDao.userUpdateFileName(map);
            if(i>0){
                //i>0说明已经保存到数据库中
                tempMap.put("status","200");
            }else{
                tempMap.put("status","500");
            }
        }
        return tempMap;
    }

    @Override
    public int userUploadFile(Map map) {
        return userFilesDao.userUploadFile(map);
    }

    @Override
    public Map getDownLoadFileInfo(int belongId, int fileId) {
        //调用dao层方法查询到下载文件的信息
        Map fileInfo = userFilesDao.getDownLoadFileInfo(belongId, fileId);
        if(fileInfo!=null && fileInfo.size()>0){
            //获取文件在ftp服务器上的文件名
            String tempFileName = fileInfo.get("fileUrlPath")+"";
            //根据文件的url截取ftp服务器上存储的文件的文件名
            String temp[] = tempFileName.replaceAll("\\\\","/").split("/");
            if (temp.length > 1) {
                String fileName = temp[temp.length - 1];
                fileInfo.put("fileName",fileName);
            }
        }
        return fileInfo;
    }

    @Override
    public int userGarbageFiles(int belongId, String[] returnedFilesIds) {
        return userFilesDao.userGarbageFiles(belongId, returnedFilesIds);
    }

}
