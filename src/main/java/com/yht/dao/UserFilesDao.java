package com.yht.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * ClassName:UserFilesDao
 * Description:
 * Author:yanht
 * CreateTime:2018-4-07 22:47
 */

public interface UserFilesDao {
    /**
     * 用户文件列表显示
     * @param map
     * @return
     */
    @Select({"<script>" +
            "SELECT " +
              "i.fileId, " +
              "i.belongId, " +
              "i.parentId, " +
              "i.fileName, " +
              "i.filePath, " +
              "i.fileType, " +
              "i.fileSize, " +
              "i.uploadDate, " +
              "i.modifyDate, " +
              "i.fileUrlPath, " +
              "i.isDeleted, "+
              "t.typeName " +
            " FROM " +
              "t_fileinfo i " +
            " LEFT JOIN " +
              "t_filetype t " +
            " ON " +
              "t.typeId = i.fileType" +
            " <where> " +
            " <if test=\"parentId != null\"> and parentId=#{parentId} </if>"+
            " <if test=\"fileType != null and fileType != ''\"> and fileType=#{fileType} </if>"+
            " <if test=\"isDeleted != null and isDeleted != ''\"> and isDeleted=#{isDeleted} </if>"+
            " <if test=\"isDeleted == null or isDeleted == ''\"> and isDeleted= 0 </if>"+
            " </where> "+
            " and belongId= #{belongId} "+
            " LIMIT " +
              "#{start},#{end}" +
            "</script>"})
    List<Map> userFileList(Map map);

    /**
     * 查询用户列表分页总数量
     * @param map
     * @return
     */
    @Select({"<script>"
            +"select count(*) from t_fileinfo "
            +" <where> "
            +" <if test=\"parentId != null\"> and parentId=#{parentId} </if>"
            +" <if test=\"fileType != null and fileType != ''\"> and fileType=#{fileType} </if>"
            +" </where> "
            +" and belongId=#{belongId} "
            +" and isDeleted= #{isDeleted}"
            +"</script>"})
    int userFileCount(Map map);

    /**
     * 新建文件夹
     * @param map
     * @return
     */
    @Insert({"<script>"
            +"insert into t_fileinfo (belongId,parentId,fileName,filePath,fileType,fileSize,uploadDate,modifyDate,isDeleted)"
            +"values (#{belongId},#{parentId},#{fileName},#{filePath},#{fileType},#{fileSize},#{uploadDate},#{modifyDate},0)"
            +"</script>"})
    int userNewFolder(Map map);

    /**
     * 检查新建文件夹名是否重复
     * @param map
     * @return
     */
    @Select({"<script>"
            +"select * from t_fileinfo where parentId=#{parentId} and fileName = #{fileName} and belongId=#{belongId}"
            +"</script>"})
    List<Map> isFileNameRepeat(Map map);


    /**
     * 用户删除文件放入回收站
     * @param belongId
     * @param deleteFilesIds
     * @return
     */
    @Delete({"<script>"
            +"update t_fileinfo set isDeleted = 1 where belongId = #{belongId} and fileId in "
            +"<foreach collection='deleteFilesIds' item='deleteFilesId' open='(' separator=',' close=')'>"
            +"#{deleteFilesId}"
            +"</foreach>"
            +" or parentId in"
            +"<foreach collection='deleteFilesIds' item='deleteFilesId' open='(' separator=',' close=')'>"
            +"#{deleteFilesId}"
            +"</foreach>"
            +"</script>"})
    int deleteFiles(int belongId,String[] deleteFilesIds);

    /**
     * 用户彻底删除文件
     * @param belongId
     * @param deleteFilesIds
     * @return
     */
    @Delete({"<script>"
            +"delete from t_fileinfo where belongId = #{belongId} and fileId in "
            +"<foreach collection='deleteFilesIds' item='deleteFilesId' open='(' separator=',' close=')'>"
            +"#{deleteFilesId}"
            +"</foreach>"
            +" or parentId in"
            +"<foreach collection='deleteFilesIds' item='deleteFilesId' open='(' separator=',' close=')'>"
            +"#{deleteFilesId}"
            +"</foreach>"
            +"</script>"})
    int userDeleteFiles(int belongId,String[] deleteFilesIds);


    /**
     * 回收站找回
     * @param belongId
     * @param returnedFilesIds
     * @return
     */
    @Update({"<script>"
            +"update t_fileinfo set isDeleted = 0 where belongId = #{belongId} and fileId in "
            +"<foreach collection='returnedFilesIds' item='returnFilesId' open='(' separator=',' close=')'>"
            +"#{returnFilesId}"
            +"</foreach>"
            +" or parentId in"
            +"<foreach collection='returnedFilesIds' item='returnFilesId' open='(' separator=',' close=')'>"
            +"#{returnFilesId}"
            +"</foreach>"
            +"</script>"})
    int userGarbageFiles(int belongId,String[] returnedFilesIds);

    /**
     * 用户更新文件名
     * @param map
     * @return
     */
    @Update({"<script>"
            +"update t_fileinfo set fileName = #{fileName} , modifyDate = #{modifyDate} where belongId = #{belongId} and fileId = #{fileId}"
            +"</script>"})
    int userUpdateFileName(Map map);

    /**
     * 用户上传文件
     * @param map
     * @return
     */
    @Insert({"<script>"
            +"insert into t_fileinfo (belongId,parentId,fileName,filePath,fileType,fileSize,uploadDate,modifyDate,fileUrlPath)"
            +"values (#{belongId},#{parentId},#{fileName},#{filePath},#{fileType},#{fileSize},#{uploadDate},#{modifyDate},#{fileUrlPath})"
            +"</script>"})
    int userUploadFile(Map map);

    /**
     * 获取下载文件的信息
     * @param belongId
     * @param fileId
     * @return
     */
    @Select({"<script>"
            +"select fileUrlPath from t_fileinfo where belongId = #{belongId} and fileId = #{fileId}"
            +"</script>"})
    Map getDownLoadFileInfo(int belongId,int fileId);
}
