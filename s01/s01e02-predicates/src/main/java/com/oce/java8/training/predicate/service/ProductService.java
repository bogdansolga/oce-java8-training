package com.oce.java8.training.predicate.service;

import com.oce.java8.training.model.Product;
import com.oce.java8.training.model.Section;
import com.oce.java8.training.bootstrap.StoreSetup;
import com.oce.java8.training.model.StoreSection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple service for managing {@link Product} entities
 *
 * @author bogdan.solga
 */
public class ProductService {

    public List<Product> getNexusTablets() {
        final Section tablets = StoreSetup.getDefaultStore()
                                          .getStoreSections()
                                          .stream()
                                          .filter(section -> section.getName().equals(StoreSection.Tablets))
                                          .findFirst()
                                          .orElseThrow(() -> new IllegalArgumentException("There's no section named 'Tablets'"));

        final List<Product> products = tablets.getProducts()
                                              .orElseThrow(() -> new IllegalArgumentException("There are no available tablets"));

        return products.stream()
                       .filter(product -> product.getName().toLowerCase().contains("nexus"))
                       .collect(Collectors.toList());
    }
}
