package com.example.demo.exception;

import com.example.demo.constants.ResultConstant;
import com.example.demo.vo.ResultVo;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/6/22
 */
@ControllerAdvice
@ResponseBody
public class SysCustomException {


    /*
     *  400-Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVo handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResultVo.error(ResultConstant.HTTP_STATUS_BAD_REQUEST,ResultConstant.HTTP_STATUS_BAD_REQUEST_VALUE);
    }


    /**
     * 404-NOT_FOUND
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResultVo handlerNotFoundException(NoHandlerFoundException e)
    {
        return ResultVo.error(ResultConstant.HTTP_STATUS_NOT_FOUND,ResultConstant.HTTP_STATUS_NOT_FOUND_VALUE);
    }

    /*
     * 405 - method Not allowed
     * HttpRequestMethodNotSupportedException 是ServletException 的子类，需要Servlet API 支持
     *
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVo handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        //log.error("不合法的请求方法...",e);
        return ResultVo.error(ResultConstant.HTTP_STATUS_METHOD_NOT_ALLOWED,ResultConstant.HTTP_STATUS_METHOD_NOT_ALLOWED_VALUE);
    }

    /**
     * 415-Unsupported Media Type.HttpMediaTypeNotSupportedException是ServletException的子类，需要Serlet API支持
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
    public ResultVo handleHttpMediaTypeNotSupportedException(Exception e) {
        //log.error("内容类型不支持...", e);
        return ResultVo.error(ResultConstant.HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE,ResultConstant.HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE_VALUE);
    }


    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResultVo handleException(Exception e) {
        return ResultVo.error(ResultConstant.HTTP_STATUS_INTERNAL_SERVER_ERROR,ResultConstant.HTTP_STATUS_INTERNAL_SERVER_ERROR_VALUE);
    }

}
