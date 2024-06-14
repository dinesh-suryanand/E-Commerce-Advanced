package com.dinesh.eproduct.product;

import com.dinesh.eproduct.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;
    public Integer createProduct(ProductRequest request) {
        var product = mapper.toProduct(request);
        return repository.save(product).getId();
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
        //1 getting ids from product request
        var productIds = request.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();
        //2 getting all the products form db
        var storedProducts = repository.findAllByIdInOrderById(productIds);
        // checking 1 and 2 are same length, so we can order the products
        if (productIds.size() !=  storedProducts.size()) throw new ProductPurchaseException("One or more Products does not exist");
        //creating order request
        var storedRequest  = request.stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();
        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();
        // adding to purchased products
        for (int i = 0; i < storedProducts.size(); i++) {
            var product = storedProducts.get(i);
            var productRequest = storedRequest.get(i);
            // if available quantity is less than ordered quantity it will throw error
            if (product.getAvailableQuantity() < productRequest.quantity()) throw new ProductPurchaseException("Insufficient stock for Quantity you ordered");
            //updating availableQuantity
            product.setAvailableQuantity(product.getAvailableQuantity() - productRequest.quantity());
            repository.save(product);
            purchasedProducts.add(mapper.toProductPurchaseResponse(product,productRequest.quantity()));

        }

        return purchasedProducts;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID:: " + productId));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
