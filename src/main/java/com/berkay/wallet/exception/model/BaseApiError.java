package com.berkay.wallet.exception.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseApiError<E> {
    private int status;
    private Exception<E> exception;
}
