package com.oce.java8.training.operators;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * A few {@link java.util.function.UnaryOperator}s and {@link java.util.function.BinaryOperator}s interfaces usage samples
 */
public class OperatorsMain {

    public static void main(String[] args) {
        unaryOperators();

        binaryOperators();
    }

    private static void unaryOperators() {
        final UnaryOperator<String> lowerCase = String::toLowerCase;
        System.out.println(lowerCase.apply("Somewhere"));

        final List<String> values = Arrays.asList("Some", "Random", "Values");
        values.replaceAll(lowerCase);

        // using a Consumer
        values.forEach(System.out::println);
    }

    private static void binaryOperators() {
        final BinaryOperator<Integer> multiplier = (first, second) -> first * second;
        System.out.println(multiplier.apply(20, 7));
    }
}