package com.example.demo.vo;


import java.io.Serializable;
import java.util.Date;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
public class ResultVo implements Serializable {

    private int result;//请求状态 返回值
    private String message = "请求成功";//请求状态信息
    private String token;//登录session返回token
    private Date systemTime = new Date();//系统当前时间
    private Object dataSet;//返回的结果集
    private Object pageInfo;//返回的分页数据

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(Date systemTime) {
        this.systemTime = systemTime;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getDataSet() {
        return dataSet;
    }

    public void setDataSet(Object dataSet) {
        this.dataSet = dataSet;
    }

    public Object getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Object pageInfo) {
        this.pageInfo = pageInfo;
    }

    //请求失败
    public static ResultVo error(int result, String message) {
        return new ResultVo(result, message, new Date());
    }

    //请求成功
    public static ResultVo success(){
        return new ResultVo(0, "请求成功", new Date());
    }

    public ResultVo() {
        super();
    }

    public ResultVo(int result, String message,Date systemTime) {
        this.result = result;
        this.message = message;
        this.systemTime = systemTime;
    }

    public ResultVo(int result, String message, String token, Date systemTime, Object dataSet, Object pageInfo) {
        this.result = result;
        this.message = message;
        this.token = token;
        this.systemTime = systemTime;
        this.dataSet = dataSet;
        this.pageInfo = pageInfo;
    }
}
