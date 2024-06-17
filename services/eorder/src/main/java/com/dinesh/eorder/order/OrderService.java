package com.dinesh.eorder.order;

import com.dinesh.eorder.customer.CustomerClient;
import com.dinesh.eorder.exception.BusinessException;
import com.dinesh.eorder.kafka.OrderConfirmation;
import com.dinesh.eorder.kafka.OrderProducer;
import com.dinesh.eorder.order.payment.PaymentClient;
import com.dinesh.eorder.order.payment.PaymentRequest;
import com.dinesh.eorder.orderline.OrderLineRequest;
import com.dinesh.eorder.orderline.OrderLineService;
import com.dinesh.eorder.product.ProductClient;
import com.dinesh.eorder.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;



//    @Transactional
    public Integer createOrder(OrderRequest request) {
        //1 checking customer --> openFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));
        //2 purchase the product  --> product-ms Rest Template
        var purchasedProducts = this.productClient.purchaseProducts(request.products());

        //3 persist order
        var order = this.repository.save(mapper.toOrder(request));

        //4 persist orderLines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        //5 todo start payment
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        //6 send the order confirmation --> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
