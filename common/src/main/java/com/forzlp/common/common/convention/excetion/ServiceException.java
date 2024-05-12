package com.forzlp.common.common.convention.excetion;


import com.forzlp.common.common.convention.errorcode.BaseErrorCode;
import com.forzlp.common.common.convention.errorcode.IErrorCode;

/**
 * Author 70ash
 * Date 2024/1/24 23:08
 * Description:
 */
public class ServiceException extends AbstractException{
    public ServiceException(String message) {
        super(message, null , BaseErrorCode.SERVICE_ERROR);
    }

    public ServiceException(IErrorCode errorCode) {
        super(null, null ,errorCode);
    }

    public ServiceException(String message, IErrorCode errorCode) {
        super(message, null ,errorCode);
    }

    public ServiceException(String message, Throwable cause, IErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
