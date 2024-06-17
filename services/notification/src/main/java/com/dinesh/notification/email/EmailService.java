package com.dinesh.notification.email;

import com.dinesh.notification.kafka.order.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dinesh.notification.email.EmailTemplates.ORDER_CONFIRMATION;
import static com.dinesh.notification.email.EmailTemplates.PAYMENT_CONFIRMATION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_RELATED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendPaymentSuccessEmail(
            String destinationEmail,
            String customerName,
            BigDecimal amount,
            String orderReference
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper =
                new MimeMessageHelper(message, MULTIPART_MODE_RELATED, UTF_8.name());

        // check email needs to be changed or not
        messageHelper.setFrom("contact@aliboucoding.com");
        final String templateName = PAYMENT_CONFIRMATION.getTemplate();

        Map<String,Object> variables = new HashMap<>();
        variables.put("customerName",customerName);
        variables.put("amount",amount);
        variables.put("orderReference",orderReference);

        Context context = new Context();

        context.setVariables(variables);
        messageHelper.setSubject(PAYMENT_CONFIRMATION.getSubject());

        try {
            String htmlTemplate = templateEngine.process(templateName,context);
            messageHelper.setText(htmlTemplate,true);
            messageHelper.setTo(destinationEmail);
            mailSender.send(message);
            log.info(String.format("INFO - Email Successfully send to :: %s with template :: %s"),destinationEmail,templateName);

        } catch (MessagingException e) {
            log.warn("WARNING - Cannot Send email to :: {}",destinationEmail);

        }
    }

    @Async
    public void sendOrderConfirmationEmail(
            String destinationEmail,
            String customerName,
            BigDecimal amount,
            String orderReference,
            List<Product> products
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper =
                new MimeMessageHelper(message, MULTIPART_MODE_RELATED, UTF_8.name());

        // check email needs to be changed or not
        messageHelper.setFrom("contact@aliboucoding.com");
        final String templateName = ORDER_CONFIRMATION.getTemplate();

        Map<String,Object> variables = new HashMap<>();
        variables.put("customerName",customerName);
        variables.put("totalAmount",amount);
        variables.put("orderReference",orderReference);
        variables.put("products",products);

        Context context = new Context();

        context.setVariables(variables);
        messageHelper.setSubject(ORDER_CONFIRMATION.getSubject());

        try {
            String htmlTemplate = templateEngine.process(templateName,context);
            messageHelper.setText(htmlTemplate,true);
            messageHelper.setTo(destinationEmail);
            mailSender.send(message);
            log.info(String.format("INFO - Email Successfully send to :: %s with template :: %s"),destinationEmail,templateName);

        } catch (MessagingException e) {
            log.warn("WARNING - Cannot Send email to :: {}",destinationEmail);

        }
    }
}
