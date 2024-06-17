package com.dinesh.notification.notification;

import com.dinesh.notification.kafka.order.OrderConfirmation;
import com.dinesh.notification.kafka.payment.PaymentConfirmation;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Document
@NoArgsConstructor
public class Notification {

    @Id
    private String id;
    private NotificationType type;
    private LocalDateTime notificationDate;
    private OrderConfirmation orderConfirmation;
    private PaymentConfirmation paymentConfirmation;


}
