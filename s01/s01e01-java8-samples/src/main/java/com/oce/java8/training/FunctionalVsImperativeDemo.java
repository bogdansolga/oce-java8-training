package com.oce.java8.training;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FunctionalVsImperativeDemo {

    public static void main(String[] args) {
        final List<String> strings = Arrays.asList("I want a holiday, not just a weekend".split(" "));

        // imperative processing
        final Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String currentValue = iterator.next();
            if (currentValue.isEmpty()) {
                iterator.remove();
            }
        }

        // functional processing
        strings.removeIf(value -> value.isEmpty());
    }
}
