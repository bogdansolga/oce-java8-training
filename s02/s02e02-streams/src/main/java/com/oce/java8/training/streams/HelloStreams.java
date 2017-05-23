package com.oce.java8.training.streams;

import com.oce.java8.training.bootstrap.StoreSetup;
import com.oce.java8.training.model.Product;
import com.oce.java8.training.model.Section;
import com.oce.java8.training.model.Store;
import com.oce.java8.training.model.StoreSection;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public class HelloStreams {

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        final List<String> holiday = Arrays.asList("I want a holiday, not just a weekend".split(" "));

        simpleStreams(holiday);

        productsStreamOperations();

        reductionOperations(holiday);

        matchingOperations(holiday);

        averageOperations(holiday);

        parallelStreams(holiday);

        usingTreeSetAsCollectorReturnType(holiday);

        numberStreams();

        streamsForMaps();
    }

    private static void simpleStreams(List<String> holiday) {
        holiday.stream().forEach(value -> System.out.println(value));
        holiday.forEach(value -> System.out.println(value));

        final Set<Integer> wordsLength = holiday.stream()
                                                .filter(value -> value.length() > 2)
                                                .map(value -> value.length())
                                                .sorted()
                                                .collect(Collectors.toSet());
    }

    private static void reductionOperations(List<String> holiday) {
        final String collectedValue = holiday.stream()
                                             .reduce("{", (first, second) -> first + " | " + second);
        System.out.println(collectedValue);
    }

    private static void productsStreamOperations() {
        final Store defaultStore = StoreSetup.getDefaultStore();

        final Section section = defaultStore.getStoreSections()
                                            .stream()
                                            .filter(it -> it.getName().equals(StoreSection.Tablets))
                                            .findFirst()
                                            .orElseThrow(() -> new IllegalArgumentException("There's no tablets section"));

        final List<Product> productList = getProducts(section);
        final Product appleTablet = productList.stream()
                                               .filter(product -> product.getName().toLowerCase().contains("apple"))
                                               .findFirst()
                                               .orElseThrow(() -> new IllegalArgumentException("No Apple tablet"));

        final List<Product> products = defaultStore.getStoreSections()
                                                   .stream()
                                                   .map(HelloStreams::getProducts)
                                                   .flatMap(Collection::stream) // all products from all sections
                                                   .filter(product -> product.getName().length() > 3)
                                                   .collect(Collectors.toList());

        final List<Product> productsWithSingleFlatMap = defaultStore.getStoreSections()
                                                                    .stream()
                                                                    .flatMap(it -> getProducts(it).stream())
                                                                    .filter(product -> product.getName().length() > 3)
                                                                    .collect(Collectors.toList());
    }

    private static void matchingOperations(final List<String> holiday) {
        boolean wordsLongerThan5Chars = holiday.stream()
                                               .anyMatch(value -> value.length() > 5);

        boolean hasNullOrEmptyValues = holiday.stream()
                                              .noneMatch(value -> value == null || value.isEmpty());
    }

    private static void averageOperations(final List<String> holiday) {
        final OptionalDouble average = holiday.stream()
                                              .distinct()
                                              .limit(3)
                                              .filter(Objects::nonNull)
                                              .mapToInt(String::length)
                                              .average();
        average.ifPresent(value -> System.out.println("The average word length is " + value));

    }

    private static void parallelStreams(List<String> holiday) {
        System.out.println("There are " + PROCESSORS + " available processors");
        System.out.println();

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(PROCESSORS / 2));

        holiday.parallelStream()
               .filter(value -> value.length() > 2)
               .sorted()
               .forEach(value -> System.out.println(Thread.currentThread().getName() + " - " + value));

        final Stream<String> stream = StreamSupport.stream(holiday.spliterator(), holiday.size() < 100);
        stream.forEach(value -> System.out.println(Thread.currentThread().getName() + " - " + value));
    }

    private static void usingTreeSetAsCollectorReturnType(List<String> holiday) {
        final TreeSet<String> treeSet = holiday.stream()
                                               .filter(value -> value.length() > 2)
                                               .collect(Collectors.toCollection(TreeSet::new));

        System.out.println(treeSet);
    }

    private static void numberStreams() {
        final OptionalDouble average = IntStream.range(0, 100)
                                                .average();
        System.out.println(average);

        final IntSummaryStatistics intSummaryStatistics = IntStream.range(0, 200)
                                                                   .parallel()
                                                                   .summaryStatistics();
        System.out.println(intSummaryStatistics);

        final OptionalDouble average1 = DoubleStream.of(10, 20, 50, 70)
                                                    .average();
    }

    private static void streamsForMaps() {
        final Map<Integer, String> months = new HashMap<Integer, String>(2) {{
            put(1, "Jan");
            put(2, "Feb");
        }};

        months.keySet().stream();
        months.values().stream();
        months.entrySet().stream();
    }

    private static List<Product> getProducts(final Section section) {
        return section.getProducts()
                      .orElse(new ArrayList<>());
    }
}
