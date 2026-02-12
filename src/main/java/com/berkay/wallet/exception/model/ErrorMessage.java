package com.berkay.wallet.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {

    private MessageType messageType;
    private String details;

    public String prepareMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(messageType.getHttpStatus().value())
                .append(" : ")
                .append(messageType.getMessage());
        if (details != null && !details.isBlank()) {
            sb.append(" : ").append(details);
        }
        return sb.toString();
    }
}
