package com.gm.demo.crawler.config;

import com.gm.model.response.JsonResult;
import com.gm.utils.base.Collection;
import com.gm.utils.base.ExceptionUtils;
import com.gm.utils.base.Logger;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jason
 */
@RestControllerAdvice
public class ThrowableHandler {

    /**
     * 所有异常报错
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value=Exception.class)
    public JsonResult allHandler(HttpServletRequest req, Exception e) {
        return Logger.error(JsonResult.unsuccessful(ExceptionUtils.getMsg(e)));
    }
    @ExceptionHandler(value=BindException.class)
    public JsonResult allHandler(HttpServletRequest req, BindException e) {
        List<FieldError> errors = e.getFieldErrors();
        List<String> msg = errors.stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return Logger.error(JsonResult.unsuccessful(Collection.toArrString(msg, ";")));
    }

}