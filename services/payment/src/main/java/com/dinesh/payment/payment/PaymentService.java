package com.dinesh.payment.payment;

import com.dinesh.payment.notification.NotificationProducer;
import com.dinesh.payment.notification.PaymentNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentsRepository repository;
    private final PaymentsMapper mapper;
    private final NotificationProducer notificationProducer;

    public Integer createPayment(PaymentRequest request) {

        var payment = repository.save(mapper.toPayment(request));

        notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        request.orderReference(),
                        request.amount(),
                        request.paymentMethod(),
                        request.customer().firstname(),
                        request.customer().lastname(),
                        request.customer().email()
                )
        );

        return payment.getId();
    }
}
