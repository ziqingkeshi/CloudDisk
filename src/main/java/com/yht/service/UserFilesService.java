package com.yht.service;

import java.util.List;
import java.util.Map;

public interface UserFilesService {
    /**
     * 用户文件列表
     * @return
     */
    List<Map> userFileList(Map map);

    /**
     * 查询分页总数量
     * @param map
     * @return
     */
    int userFileCount(Map map);

    /**
     * 新建文件夹
     * @param map
     * @return
     */
    Map userNewFolder(Map map);

    /**
     * 用户删除文件放入回收站
     * @param belongId
     * @param deleteFilesIds
     * @return
     */
    int dleteFiles(int belongId,String[] deleteFilesIds);

    /**
     * 用户批量删除文件
     * @param belongId
     * @param deleteFilesIds
     * @return
     */
    int userDeleteFiles(int belongId,String[] deleteFilesIds);

    /**
     * 检查新建文件夹名是否重复
     * @param map
     * @return
     */
    List<Map> isFileNameRepeat(Map map);

    /**
     * 用户更新文件名
     * @param map
     * @return
     */
    Map userUpdateFileName(Map map);

    /**
     * 用户上传文件
     * @param map
     * @return
     */
    int userUploadFile(Map map);

    /**
     * 获取下载文件的信息
     * @param belongId
     * @param fileId
     * @return
     */
    Map getDownLoadFileInfo(int belongId,int fileId);

    /**
     * 回收站找回
     * @param belongId
     * @param returnedFilesIds
     * @return
     */
    int userGarbageFiles(int belongId,String[] returnedFilesIds);
}
