package com.oce.java8.training.supplier;

import com.oce.java8.training.model.Product;
import com.oce.java8.training.supplier.service.ProductService;

import java.util.Random;
import java.util.function.Supplier;

/**
 * A few {@link java.util.function.Supplier} usage samples
 */
public class SuppliersMain {

    public static void main(String[] args) {
        simpleSuppliers();

        productSuppliers();

        sectionSuppliers();

        managerSuppliers();
    }

    private static void simpleSuppliers() {
        final Supplier<Double> doubleSupplier = () -> new Random(2525325).nextDouble();
        System.out.println(doubleSupplier.get());

        final Supplier<String> holidaySupplier = () -> "I want a holiday, not just a weekend :)";
        System.out.println(holidaySupplier.get());

        final Supplier<RuntimeException> runtimeExceptionSupplier = () ->
                                                new IllegalArgumentException("Nope");
        System.err.println(runtimeExceptionSupplier.get().getMessage());

        // TODO try other simple Suppliers - String, Boolean, ...
    }

    private static void productSuppliers() {
        final ProductService productService = new ProductService();

        final Product product = productService.generateRandomProduct().get();
        System.out.println(product);
    }

    private static void sectionSuppliers() {
        // TODO create a method to generate a random section object
    }

    private static void managerSuppliers() {
        // TODO create a method to generate a random manager object
    }
}