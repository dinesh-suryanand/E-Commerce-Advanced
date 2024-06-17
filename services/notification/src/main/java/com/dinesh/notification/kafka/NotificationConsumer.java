package com.dinesh.notification.kafka;

import com.dinesh.notification.email.EmailService;
import com.dinesh.notification.kafka.order.OrderConfirmation;
import com.dinesh.notification.kafka.payment.PaymentConfirmation;
import com.dinesh.notification.notification.Notification;
import com.dinesh.notification.notification.NotificationRepository;
import com.dinesh.notification.notification.NotificationType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    // consume payments notifications
    private final NotificationRepository notificationRepository;

    private final EmailService emailService;
    @KafkaListener(topics = "payment-topic")
    private void consumePaymentSuccessNotification(PaymentConfirmation paymentConfirmation) throws MessagingException {
        log.info(String.format("Consuming the message from payment topic :: %s",paymentConfirmation));

        notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );

        // send email
        var customerName = paymentConfirmation.customerFirstname() + " " + paymentConfirmation.customerLastname();

        emailService.sendPaymentSuccessEmail(paymentConfirmation.customerEmail(),customerName,
                paymentConfirmation.amount(), paymentConfirmation.orderReference());

    }

    // consume order notifications

    @KafkaListener(topics = "order-topic")
    private void consumePaymentSuccessNotification(OrderConfirmation orderConfirmation) throws MessagingException {
        log.info(String.format("Consuming the message from order topic :: %s",orderConfirmation));

        notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.ORDER_CONFIRMATION )
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );

        // send email
        var customerName = orderConfirmation.customer().firstname() + " " + orderConfirmation.customer().lastname();

        emailService.sendOrderConfirmationEmail(orderConfirmation.customer().email(),customerName,
                orderConfirmation.totalAmount(), orderConfirmation.orderReference(),orderConfirmation.products());

    }
}
