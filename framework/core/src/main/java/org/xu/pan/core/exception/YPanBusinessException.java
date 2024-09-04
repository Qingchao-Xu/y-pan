package org.xu.pan.core.exception;

import lombok.Data;
import org.xu.pan.core.response.ResponseCode;

/**
 * 自定义全局业务异常类
 */
@Data
public class YPanBusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    public YPanBusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getDesc();
    }

    public YPanBusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public YPanBusinessException(String message) {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = message;
    }

    public YPanBusinessException() {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = ResponseCode.ERROR_PARAM.getDesc();
    }

}

