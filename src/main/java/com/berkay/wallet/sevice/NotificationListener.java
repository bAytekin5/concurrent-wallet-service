package com.berkay.wallet.sevice;

import com.berkay.wallet.dto.TransferEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationListener {

    @RabbitListener(queues = "${rabbitmq.queue.transfer}")
    public void transferNotification(TransferEvent event) {
        log.info("==================================================");
        log.info(" RABBITMQ EVENT RECEIVED! Starting asynchronous processing...");
        log.info(" Receiver Wallet [{}]: {} TRY has been credited to your account.", event.receiverWalletId(), event.amount());
        log.info(" Sender Wallet [{}]: {} TRY has been debited from your account.", event.senderWalletId(), event.amount());
        log.info("==================================================");

    }
}
