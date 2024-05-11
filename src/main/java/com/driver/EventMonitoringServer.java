package com.driver;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventMonitoringServer {
	 private static final int THREAD_POOL_SIZE = 5;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private static final AtomicBoolean highMagnitudeEventDetected = new AtomicBoolean(false);

    public static void main(String[] args) {
        try {
            startServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    private static void startServer() throws InterruptedException {
        System.out.println("Event monitoring server started. Enter 'shutdown' to stop the server manually.");

        // Simulating event processing
        for (int i = 1; i <= 10; i++) {
            final int eventId = i;
            executorService.submit(() -> processEvent(eventId));
        }

        // Wait for the shutdown signal
        waitForShutdownSignal();
    }

    private static void processEvent(int eventId) {
        // Simulate event processing
        System.out.println("Event " + eventId + " processed.");
        if (eventId == 6) {
            highMagnitudeEventDetected.set(true);
            System.out.println("High magnitude event detected!");
        }
    }

    private static void waitForShutdownSignal() throws InterruptedException {
        String userInput;
        while (true) {
            userInput = getUserInput();
            if (userInput.equals("shutdown")) {
                shutdownLatch.countDown();
                break;
            }
        }
    }

    private static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static void stopServer() {
        try {
            // Wait for all submitted tasks to complete
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);

            // If high magnitude event detected, print message
            if (highMagnitudeEventDetected.get()) {
                System.out.println("Shutting down the server gracefully...");
            } else {
                // If not, indicate manual shutdown
                System.out.println("Manual shutdown initiated. Shutting down the server...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shutdown the executor service forcefully if not terminated
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        }
    }
}
