package com.berkay.wallet.exception.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Exception<E> {

    private String hostName;
    private String path;
    private LocalDateTime createTime;
    private E message;
}
