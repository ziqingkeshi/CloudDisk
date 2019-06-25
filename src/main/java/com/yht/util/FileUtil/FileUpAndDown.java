package com.yht.util.FileUtil;


import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;


/**
 * ClassName:FileUpAndDown
 * Description:
 * Ftp上传服务器的步骤：
 * 第一步，创建一个客户端对象，
 * 第二步，连接服务器，地址，端口，
 * 第三步，实现登录信息，用户名，密码
 * 第四步，创建上传的文件文件夹 同时创建上传路径
 * 第五步，指定ftp上传路径
 * 第六步，指定上传文件类型为二进制类型
 * 第七步，读取要上传的本地文件路径，同时创建输入流
 * 第八步，用客户端上传本地文件，参数设置文件名跟输入流
 * 第九步，处理异常，关闭文件流，断开服务器连接。
 * Author:严浩天
 * CreateTime:2019-04-12 14:38
 */
@Component
public class FileUpAndDown {
    //私有构造
    private FileUpAndDown(){};
    //私有静态属性
    private static FileUpAndDown fileUpAndDown;
    //懒汉模式
    public static FileUpAndDown getInstance(){
        if(fileUpAndDown==null){
            fileUpAndDown = new FileUpAndDown();
        }
        return fileUpAndDown;
    }

    @Value("${remoteIp}")
    private String remoteIp;

    @Value("${uploadPort}")
    private int uploadPort;

    @Value("${ftpUserName}")
    private  String ftpUserName;

    @Value("${ftpPassWord}")
    private  String ftpPassWord;

    @Value("${remotePath}")
    private  String remotePath;

    /**
     * 将图片上传到ftp远程服务器
     */
    public String upLoad(MultipartFile multipartFile){
        //创建客户端对象
        FTPClient ftp = new FTPClient();
        InputStream local = null;
        String newFileName = null;
        try {
            //连接ftp服务器
            ftp.connect("39.96.8.65",21);
            //登录
            ftp.login("ftpadmin","ziqingkeshi");
            //设置上传路径
            String path = "/home/ftpadmin/cloudDisk";
            //检查上传路径是否存在 如果不存在返回false
            boolean flag = ftp.changeWorkingDirectory(path);
            if(!flag){
                //创建上传的路径 该方法只能创建一级目录,在这里如果/home/ftpadmin存在则可以创建image
                ftp.makeDirectory(path);
            }
            //指定上传路径
            ftp.changeWorkingDirectory(path);
            //指定上传文件的类型 二进制文件
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

           // MultipartFile multipartFile=null;
            //获取文件的绝对路径
            //String absolutePath = multipartFile.getResource().getFile().getAbsolutePath();
            //System.out.println(absolutePath+"绝对路径。。。。。。。。。。。");

            String originalFilename = multipartFile.getOriginalFilename();
            newFileName= UUID.randomUUID()+originalFilename.substring(originalFilename.lastIndexOf("."));
            //读取本地文件
//            File file =new File("/Users/ae/Desktop/CloudDiskFiles"+File.separator+newFileName);
//            File file1 = new File("/Users/ae/Desktop/上传的文件");
//            if(!file1.exists()){//如果文件夹不存在
//                file1.mkdir();//创建文件夹
//            }
            File file =new File("/Users/ae/Desktop/上传的文件"+File.separator+newFileName);
            //multipartFile.transferTo(file);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(multipartFile.getInputStream(),file);
            System.out.println(file.length()+"............文件长度");
            local = new FileInputStream(file);
            //第一个参数是文件名
            ftp.storeFile(file.getName(),local);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                //关闭文件流
                if(local!=null)
                local.close();
                //退出
                if(ftp!=null) {
                    ftp.logout();
                    //断开连接
                    ftp.disconnect();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }//finally
        System.out.println(newFileName+"文件的名字");
        return newFileName;
    }

    /**
     * Description: 从FTP服务器下载文件
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName 要下载的文件名
     * @param realFileName 真实的文件名
     * @return
     */
    public static byte[] downloadFile(String url, int port, String username, String password, String remotePath,
                                       String fileName,String realFileName) {
        InputStream in = null;
        File file = null;
        FTPClient ftp = new FTPClient();
        byte[] b = null;
        try {
            int reply;

            // 连接FTP服务器
            if (port > -1) {
                ftp.connect(url, port);
            } else {
                ftp.connect(url);
            }

            ftp.login(username, password);//登录
            reply = ftp.getReplyCode();
            System.out.println("登录ftp是否成功："+reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new Exception("与服务器连接异常");
            }
            ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    System.out.println("哼"+ff.getName());
                    System.out.println("哈"+fileName);
                    System.out.println("进到查找文件的循环中去了");
                    ftp.enterLocalPassiveMode();
                    ftp.setBufferSize(1024);
                    in = ftp.retrieveFileStream(fileName);
                    if(in==null){
                        System.out.println("文件流为空了，文件没有从ftp上获取到");
                        System.out.println(ftp.getReply());
                        System.out.println(ftp.getReplyCode());
                        System.out.println(ftp.getReplyString());
                        System.out.println(ftp.getReplyStrings());
                    }
                    System.out.println(in.available());
                    b = IOUtils.toByteArray(in);
                    System.out.println("字节流的大小"+b.length);
                    //手动关闭 再调用completePendingCommand
                    in.close();
                    ftp.completePendingCommand();
//                    System.out.println("输入流："+in);
//                    b = IOUtils.toByteArray(in);
//                    System.out.println("下载方法中的 byte[] b的长度为："+b);
//                    System.out.println("下载方法中的 byte[] b的长度为："+b.length);
//                    System.out.println("下载方法中的 byte[] b的长度为："+b.toString());
//                    return in;
//                    out = new BufferedOutputStream(new FileOutputStream(new File(localFile + File.separator + realFileName)));
//                    ftp.retrieveFile(ff.getName(), out);
//                    out.close();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (ftp.isConnected())
            {
                try
                {
                    ftp.disconnect();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return b;
    }


    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*4];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * 文件上传
     * @param multipartFile
     * @return
     */
    public String testUpLoad(MultipartFile multipartFile){
//            String path = "/home/ftpadmin/cloudDisk";
        String newFileName = null;
        BufferedOutputStream out =null;
        String originalFilename = multipartFile.getOriginalFilename();

        newFileName= UUID.randomUUID()+originalFilename.substring(originalFilename.lastIndexOf("."));
        try {
            out = new BufferedOutputStream(new FileOutputStream(new File(File.separator+"home"+File.separator+"ftpadmin"+File.separator+"cloudDisk"+File.separator+newFileName)));
            out.write(multipartFile.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFileName;
   }

    /**
     * 下载
     * @param fileName
     * @return
     */
    public static byte[] testDownload(String fileName) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        byte[] b = null;
        try {
            fis = new FileInputStream(File.separator + "home" + File.separator + "ftpadmin" + File.separator + "cloudDisk" + File.separator + fileName);
//            fis = new FileInputStream(File.separator + "Users" + File.separator + "ae" + File.separator + "Desktop" + File.separator + "favicon.ico");
            bis = new BufferedInputStream(fis, 1000000);
            //关闭流 先打开的后关闭 后打开的先关闭
            b = toByteArray(bis);
            //System.out.println("字节流长度："+b.length);
            bis.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
}

