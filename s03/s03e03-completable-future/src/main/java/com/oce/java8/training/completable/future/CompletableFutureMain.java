package com.oce.java8.training.completable.future;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A few {@link java.util.concurrent.CompletableFuture} usage samples
 */
public class CompletableFutureMain {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private static final Executor EXECUTOR = Executors.newWorkStealingPool(AVAILABLE_PROCESSORS / 2);

    public static void main(String[] args) {
        helloSimpleCompletableFutures();

        simpleCompletableFutures();

        chainedCompletionStages();

        simpleProductsOperations();

        moreComplexProductsOperations();

        completingMultipleCompletionStages();

        shutdownExecutor();
    }

    private static void helloSimpleCompletableFutures() {
        final CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            displayCurrentThread();
            return "I will run on Saturday";
        });
        System.out.println(completableFuture.join());

        completableFuture.thenAcceptAsync(value -> {
            displayCurrentThread();
            System.out.println("The received value is " + value);
        });

        final CompletableFuture<String> exceptionally = completableFuture.exceptionally(ex -> "Some exception occurred");
        System.out.println(exceptionally.join());

        String processingResult = completableFuture.join();
        System.out.println("The processing returned " + processingResult);
    }

    private static void simpleCompletableFutures() {
        final CompletableFuture<String> completableFuture =
                CompletableFuture.supplyAsync(() -> "a very simple text");

        final Consumer<String> stringConsumer = stringPrinter();
        completableFuture.thenAcceptAsync(stringConsumer);

        final CompletableFuture<String> anotherFuture =
                CompletableFuture.supplyAsync(() -> "another text");

        completableFuture.exceptionally(throwable -> "Thrown: " + throwable.getMessage());

        completableFuture.thenApplyAsync(String::toUpperCase, Executors.newCachedThreadPool());
        completableFuture.acceptEither(anotherFuture, stringConsumer);
    }

    private static void chainedCompletionStages() {
        CompletableFuture<String> first = CompletableFuture.supplyAsync(() -> {
            displayCurrentThread();
            return "first";
        }, EXECUTOR);

        CompletableFuture<String> second = CompletableFuture.supplyAsync(() -> {
            displayCurrentThread();
            return "second";
        }, EXECUTOR);

        CompletableFuture<Integer> third = CompletableFuture.supplyAsync(() -> {
            displayCurrentThread();
            return 7;
        }, EXECUTOR);

        final CompletableFuture<Integer> future =
                first.thenComposeAsync(value -> second)
                     .thenComposeAsync(value -> third);

        System.out.println(future.join());

        final CompletableFuture<Void> finalFuture = CompletableFuture.allOf(first, second);
        finalFuture.thenAccept(value -> notifyFinishedTasks());
    }

    private static void simpleProductsOperations() {
        final ProductProcessor productProcessor = new ProductProcessor();

        final CompletableFuture<Long> getProductsStock = productProcessor.getProductsStock("iPad");
        final Function<Long, CompletableFuture<Double>> getProductsPrice = productProcessor.getProductsPrice();
        final Function<Double, CompletableFuture<String>> getProductsDisplayText = productProcessor.getDisplayedText();

        /*
            The three processing stages:
                - 1) get products stock
                - 2) get products price, for the resulted stock
                - 3) get the displayed text, for the products price and stock
        */

        final String productsText = getProductsStock.thenComposeAsync(getProductsPrice, EXECUTOR)
                                                    .thenComposeAsync(getProductsDisplayText, EXECUTOR)
                                                    .exceptionally(Throwable::getMessage) // further error handling can be defined
                                                    .join();
        System.out.println(productsText);
    }

    private static void moreComplexProductsOperations() {
        final ProductProcessor productProcessor = new ProductProcessor();

        final CompletableFuture<Long> getProductsStock = productProcessor.getProductsStock("iPad");
        final CompletableFuture<Long> getReserveStock = productProcessor.getReserveStock("iPad");
        final Function<Long, CompletableFuture<Double>> getProductsPrice = productProcessor.getProductsPrice();
        final Function<Double, CompletableFuture<String>> getProductsDisplayText = productProcessor.getDisplayedText();

        /*
            The five processing stages:
                - 1) get products stock OR get the reserve stock (whichever finishes first)
                - 2) get products price, for the resulted stock
                - 3) get the displayed text, for the products price and stock
                - 4) when either the displayed text or an exception is returned, complete the stage asynchronously
        */

        final String productsText = getProductsStock.applyToEitherAsync(getReserveStock, Function.identity(), EXECUTOR)
                                                    .thenComposeAsync(getProductsPrice, EXECUTOR)
                                                    .thenComposeAsync(getProductsDisplayText, EXECUTOR)
                                                    .whenCompleteAsync(CompletableFutureMain::processResult, EXECUTOR)
                                                    .join();
        System.out.println(productsText);
    }

    private static void completingMultipleCompletionStages() {
        final List<CompletableFuture<String>> completableFutures =
                Stream.of(50, 60, 70, 23, 35, 13, 11, 16, 53)
                      .map(value -> CompletableFuture.supplyAsync(() ->
                              Thread.currentThread().getName() + " - " + value * value + " - " + System.currentTimeMillis()))
                      .collect(Collectors.toList());

        final CompletableFuture<Void> allStages = CompletableFuture.allOf(
                completableFutures.toArray(new CompletableFuture[completableFutures.size()]));

        final CompletableFuture<List<String>> futureValues =
                allStages.thenApply(value -> completableFutures.stream()
                                                               .map(CompletableFuture::join)
                                                               .collect(Collectors.toList()));
        futureValues.join()
                    .forEach(System.out::println);
    }

    private static void processResult(final String result, final Throwable exception) {
        if (exception != null) {
            throw new RuntimeException(exception.getMessage());
        } else {
            CompletableFuture.supplyAsync(() -> result, EXECUTOR);
        }
    }

    private static void displayCurrentThread() {
        System.out.println(Thread.currentThread().getName());
    }

    private static void notifyFinishedTasks() {
        System.out.println(Thread.currentThread().getName() + " - All good");
    }

    private static Consumer<String> stringPrinter() {
        return value -> System.out.println(Thread.currentThread().getName() + " - " + value);
    }

    private static void shutdownExecutor() {
        ((ExecutorService) EXECUTOR).shutdown();
        //System.out.println("The executor was properly shutdown");
    }
}