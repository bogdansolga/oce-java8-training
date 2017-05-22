package com.oce.java8.training.function.service;

import com.oce.java8.training.bootstrap.StoreSetup;
import com.oce.java8.training.model.Product;
import com.oce.java8.training.model.Section;
import com.oce.java8.training.model.StoreSection;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A simple service for managing {@link Product} entities
 *
 * @author bogdan.solga
 */
public class ProductService {

    public Set<String> getSamsungTabletDescriptions() {
        final Section tablets = StoreSetup.getDefaultStore()
                                          .getStoreSections()
                                          .stream()
                                          .filter(section -> section.getName().equals(StoreSection.Tablets))
                                          .findFirst()
                                          .orElseThrow(() -> new IllegalArgumentException("There's no section named 'Tablets'"));

        final List<Product> products = tablets.getProducts()
                                              .orElseThrow(() -> new IllegalArgumentException("There are no products"));

        return products.stream()
                       .filter(samsungProducts())
                       .map(displayProduct())
                       .collect(Collectors.toSet());
    }

    private Predicate<Product> samsungProducts() {
        return product -> product.getName().contains("Samsung");
    }

    private Function<Product, String> displayProduct() {
        return product -> "The product is: " + product.toString();
    }
}
