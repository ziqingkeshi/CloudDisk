package com.yht.controller;

import com.yht.service.UserFilesService;
import com.yht.util.FileUtil.FileUpAndDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.yht.util.FileUtil.FileTypeUtil.getType;
import static com.yht.util.FileUtil.FileUpAndDown.testDownload;

/**
 * ClassName:UserFilesController
 * Description:
 * Author:严浩天
 * CreateTime:2019-04-07 23:55
 */

@Controller
public class UserFilesController {

    @Autowired
    private UserFilesService userFilesService;

    @Autowired
    private HttpSession session;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 用户文件列表
     * @param map
     * @return
     */
    @RequestMapping("/userFilesList")
    @ResponseBody
    public Map page(@RequestBody Map map){
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //只查找belongId属于当前登录用户userid的文件
        map.put("belongId",userId);
        Map rmap=new HashMap();
        //文件列表数据
        rmap.put("data",userFilesService.userFileList(map));
        //文件列表总数量
        rmap.put("total",userFilesService.userFileCount(map));
        return rmap;
    }

    /**
     * 新建文件夹
     * 检测新建文件夹名手否重复
     * @param map
     * @return
     */
    @RequestMapping("/userNewFolder")
    @ResponseBody
    public Map userNewFolder(@RequestBody Map map){
        //调用service层新建文件夹方法
        Map userNewFolder = userFilesService.userNewFolder(map);
        //定义结果map用来向前台回参
        Map resultMap=new HashMap();
        //根据返回值判断文件名是否重复
        String isRepeat = userNewFolder.get("isRepeat").toString();
        //根据返回值判断保存到数据库是否成功
        if("yes".equals(isRepeat)){
            //文件名重复
            resultMap.put("status","300");
        }else{
            resultMap.put("status",userNewFolder.get("status"));
        }
        //否则直接返回resultMap结果集
        return resultMap;
    }


    /**
     * 用户将文件删除到垃圾箱
     * @param deleteFilesIds
     * @return
     */
    @RequestMapping("/deleteFiles")
    @ResponseBody
    public Map deleteFiles(@RequestBody String[] deleteFilesIds){
        Map<String,String> resultMap = new HashMap<>();
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //如果session中的userId不为空
        if (userId!=null && userId!=""){
            int belongId = Integer.valueOf(userId+"");
            //调用service层删除文件方法
            int i = userFilesService.dleteFiles(belongId, deleteFilesIds);
            //删除成功
            if (i > 0 ) {
                resultMap.put("status", "200");
            }
        }else {
            //删除失败  不管userId为空还是删除方法失败
            resultMap.put("status", "500");
        }
        return resultMap;
    }


    /**
     * 用户批量删除文件
     * @param deleteFilesIds
     * @return
     */
    @RequestMapping("/userDeleteFiles")
    @ResponseBody
    public Map userDeleteFiles(@RequestBody String[] deleteFilesIds){
        Map<String,String> resultMap = new HashMap<>();
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //如果session中的userId不为空
        if (userId!=null && userId!=""){
            int belongId = Integer.valueOf(userId+"");
            //调用service层删除文件方法
            int i = userFilesService.userDeleteFiles(belongId, deleteFilesIds);
            //删除成功
            if (i > 0 ) {
                resultMap.put("status", "200");
            }
        }else {
            //删除失败  不管userId为空还是删除方法失败
            resultMap.put("status", "500");
        }
        return resultMap;
    }

    /**
     * 回收站找回
     * @param returnedFilesIds
     * @return
     */
    @RequestMapping("/userGarbageFiles")
    @ResponseBody
    public Map userGarbageFiles(@RequestBody String[] returnedFilesIds){
        //定义一个resultMap当做回参
        Map<String,String> resultMap = new HashMap<>();
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //只查找belongId属于当前登录用户userid的文件
        int i = userFilesService.userGarbageFiles(Integer.valueOf(userId + ""), returnedFilesIds);
        if (i > 0 ) {
            resultMap.put("status", "200");
        }else {//找回失败
            resultMap.put("status", "500");
        }
        return  resultMap;
    }

    /**
     * 用户修改文件名
     * @param map
     * @return
     */
    @RequestMapping("/userUpdateFileName")
    @ResponseBody
    public Map userUpdateFileName(@RequestBody Map map){
//        System.out.println("前台传过来的文件名：" + map.get("fileName"));
//        System.out.println("前台传过来的文件Id："+map.get("fileId"));
        //调用service层更新文件名称方法
        Map updateFileName = userFilesService.userUpdateFileName(map);
        //定义结果map用来向前台回参
        Map resultMap=new HashMap();
        //根据返回值判断文件名是否重复
        String isRepeat = updateFileName.get("isRepeat").toString();
        //根据返回值判断修改文件名方法是否成功
        if("yes".equals(isRepeat)){
            //文件名重复
            resultMap.put("status","300");
        }else{
            resultMap.put("status",updateFileName.get("status"));
        }
        //否则直接返回resultMap结果集
        return resultMap;
    }

    /**
     * 文件上传ftp服务器并返回文件名称
     * @param file
     * @return
     */
    @RequestMapping("/fileUpload")
    @ResponseBody
    public Map<String,String> fileUpload(@RequestParam MultipartFile file,
                                         @RequestParam String fileName,
                                         @RequestParam String parentId,
                                         @RequestParam String filePath,
                                         @RequestParam String uploadDate,
                                         @RequestParam String modifyDate){
        //定义一个resultMap当做返回参数
        Map<String,String> resultMap = new HashMap<>();
        //定义一个paramMap当做方法入参
        Map<String,Object> paramMap = new HashMap<>();
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //格式化uploadDate
        paramMap.put("uploadDate",uploadDate);
        paramMap.put("modifyDate",modifyDate);
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //只查找belongId属于当前登录用户userid的文件
        paramMap.put("belongId", userId);
        //父文件id
        paramMap.put("parentId", parentId);
        //文件层级
        paramMap.put("filePath", filePath);
        //利用ftp上传文件 并返回文件名

        String ftpFileName = FileUpAndDown.getInstance().testUpLoad(file);

        //将返回的文件名放入map中
        paramMap.put("fileName",fileName);
        //文件的存放路径 ftp目录下加文件名/ home/ftpadmin/cloudDisk+ftpFileName
        paramMap.put("fileUrlPath","/home/ftpadmin/cloudDisk/"+ftpFileName);
        //根据要上传文件的后缀判断文件的类型
        Integer fileType = getType(fileName);
        //新建文件夹默认大小是0  此处当成类型存放
        paramMap.put("fileSize", fileType);
        switch (fileType){
            case 1 :
                paramMap.put("fileType", 2); //fileType=2代表文件类型是图片
                break;
            case 2 :
                paramMap.put("fileType", 3); //fileType=3代表文件类型是文档
                break;
            case 3 :
                paramMap.put("fileType", 4); //fileType=4代表文件类型是视频
                break;
            case 4 :
                paramMap.put("fileType", 5); //fileType=5代表文件类型是音频
                break;
            case 5 :
                paramMap.put("fileType", 6); //fileType=6代表文件类型是其他
                break;
            default :
                paramMap.put("fileType", 6); //fileType=6代表文件类型是其他
        }
        //将上传的文件信息保存到数据库
        int i = userFilesService.userUploadFile(paramMap);
        //System.out.println("上传文件保存数据库是否成功"+i);
        //保存成功
        if (i > 0 ) {
            resultMap.put("status", "200");
        }else {//保存失败
            resultMap.put("status", "500");
        }
        return  resultMap;
    }

    /**
     * 文件下载
     * @param fileName
     * @param fileId
     * @return
     */
    @RequestMapping("/fileDownload")
    public void fileDownload(HttpServletResponse response,
                             HttpServletRequest request,
                             @RequestParam String fileName,
                             @RequestParam String fileId){
        byte[] downloadFile = null;
        //System.out.println(fileName+"----"+fileId);
        //定义一个resultMap当做返回参数
        Map<String,String> resultMap = new HashMap<>();
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //System.out.println("userId"+userId);
        //如果session中的userId不为空
        if (userId!=null && userId!=""){
            int belongId = Integer.valueOf(userId+"");
            Map downLoadFileInfo = userFilesService.getDownLoadFileInfo(belongId, Integer.valueOf((fileId + "")));
//            downloadFile = FileUpAndDown.downloadFile("ip地址", 21, "ftp用户名",
////                    "ftp密码", "/home/ftpadmin/cloudDisk",
////                    (downLoadFileInfo.get("fileName") + ""), fileName);//此处最后不用加/ 方法中加了/
            downloadFile = testDownload((downLoadFileInfo.get("fileName") + ""));
            if(downloadFile != null) {
                response.reset();
                response.setContentType("application/octet-stream;charset = UTF-8");
//                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
//                response.setHeader("content-type", "application/octet-stream");
//                response.setContentType("application/octet-stream");
                // 下载文件能正常显示中文
                try {
//                    response.setHeader("Content-Disposition", "attachment;filename=" + new String( fileName.getBytes("gb2312"), "ISO8859-1" ));
                    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                    response.getOutputStream().write(downloadFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 图片预览方法
     * @param fileId
     * @return
     */
    @RequestMapping("/showPicture")
    @ResponseBody
    public Map<String,String> showPicture(@RequestBody Map fileId) {
        //System.out.println(fileId);
        //获取session中的用户id
        Object userId = session.getAttribute("userid");
        //定义远程服务器上的文件的名称
        String remoteFileName = "";
        //定义一个resultMap当做返回参数
        Map<String,String> resultMap = new HashMap<>();
        if (userId != null && userId != "") {
            int belongId = Integer.valueOf(userId + "");
//            Map downLoadFileInfo = userFilesService.getDownLoadFileInfo(belongId, Integer.valueOf((fileId + "")));
            Map downLoadFileInfo = userFilesService.getDownLoadFileInfo(belongId, Integer.valueOf(fileId.get("fileId")+""));
            remoteFileName = downLoadFileInfo.get("fileName") + "";
            //远程服务器上的文件名
            resultMap.put("remoteFileName", remoteFileName);
            resultMap.put("status","200");
        }else{
            resultMap.put("status", "500");
        }
        return  resultMap;
    }
}
