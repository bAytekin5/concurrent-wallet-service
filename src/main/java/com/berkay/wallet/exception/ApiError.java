package com.berkay.wallet.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class ApiError implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String errCode;
    private String message;
    private Integer status;
    @Setter(AccessLevel.NONE)
    private String url = "Not available";
    @Setter(AccessLevel.NONE)
    private String reqMethod = "Not available";
    @Setter(AccessLevel.NONE)
    private Instant timestamp;

    public ApiError setUrl(String url) {
        if (Strings.isNotBlank(url))
            this.url = url;
        return this;
    }

    public ApiError setReqMethod(String method) {
        if (Strings.isNotBlank(method))
            this.reqMethod = method;
        return this;
    }

    public ApiError setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

}
