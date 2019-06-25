package com.yht.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * className:PageUtil
 * discriptoin: 通用分页类
 * author:ZHEN
 * createTime:2018-11-07 15:56
 */
public class PageUtil {

    //属性  分页的要素
    private int pageNo;//第几页
    private int pageSize;//每页显示数量
    private int totalCount;//进行分页数据总数量

    private String uri;//请求的地址

    private String stringPage;//封装之后的分页字符串，该类最终返回的字符串
    /**
     * 构建对象
     * @param pageNo
     * @param pageSize
     * @param totalCount
     */
    public PageUtil(int pageNo, int pageSize, int totalCount, HttpServletRequest request) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalCount = totalCount;

        //获取请求地址
        uri = request.getRequestURI()+"?";// /web20180904/jsp/page/page4.jsp?pageNo=1&userName=a&gender=1&aaa=111&bbb=222
        //获取所有请求参数，拼装url
        Enumeration<String> parameterNames = request.getParameterNames();//获取所有的参数名称集合 [pageNo,userName,gender,aaa,bbb]
        //循环遍历
        while(parameterNames.hasMoreElements()){
            String param = parameterNames.nextElement();
            if(!"pageNo".equals(param)){//因为每次请求的url后面必然自带pageNo,所以该参数不用拼接
                if(uri.charAt(uri.length()-1)=='?'){//判断url地址的最后一位是否是？
                    uri+=param+"="+request.getParameter(param);
                }else{
                    uri+="&"+param+"="+request.getParameter(param);
                }
            }
        }
        //拼装完成后，如果最后一个不是？拼装一个&,为下面组装stringPage做准备
        if(uri.charAt(uri.length()-1)!='?'){
            uri+="&";
        }
    }

    /**
     * 为  stringPage属性添加get方法，目的拿到值
     * @return
     */
    public String getStringPage() {

        StringBuffer stringBuffer = new StringBuffer();
        //计算最大页
        int maxPage = totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1;
        //如果当前页小于1，等于1
        if(pageNo<1){
            pageNo=1;
        }
        //拼装上一页和首页
        if(pageNo>1){
            // uri =/web20180904/jsp/page/page4.jsp?userName=a&gender=1&aaa=111&bbb=222& 有参数
            // uri =/web20180904/jsp/page/page4.jsp?  没有参数
            stringBuffer.append("<a href='"+uri+"pageNo=1'>首页</a>&nbsp;<a href='"+uri+"pageNo="+(pageNo-1)+"'>上一页</a>");
        }else{//第一页
            stringBuffer.append("首页&nbsp;上一页");
        }
        //如果当前页大于最大页 等于最大页
        if(pageNo>maxPage){
            pageNo = maxPage;
        }
        //拼装下一页和尾页
        if(pageNo<maxPage){
            stringBuffer.append("&nbsp;<a href='"+uri+"pageNo="+(pageNo+1)+"'>下一页</a>&nbsp;<a href='"+uri+"pageNo="+maxPage+"'>尾页</a>");
        }else{//是最后一页
            stringBuffer.append("&nbsp;下一页&nbsp;尾页");
        }
        //拼装跳转到第几页
        stringBuffer.append("&nbsp;<select onchange=\"javascript:location.href='"+uri+"pageNo='+this.value\">");
        for(int i=1;i<=maxPage;i++){
            if(i==pageNo)
                stringBuffer.append("<option selected='selected' value='"+i+"'>"+i+"</option>");
            else
                stringBuffer.append("<option value='"+i+"'>"+i+"</option>");
        }
        stringBuffer.append("</select>&nbsp;");
        stringBuffer.append("共"+totalCount+"条"+maxPage+"页");
        return stringBuffer.toString();
    }

}
