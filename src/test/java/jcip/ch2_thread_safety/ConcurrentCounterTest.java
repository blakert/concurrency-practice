package jcip.ch2_thread_safety;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentCounterTest {
    public static boolean headerPrinted = false;
    @ParameterizedTest(name = "Test: {1}")
    @MethodSource("counterImplementations")
    public void multipleThreadsVerifyBrokenCounterFails(Counter counter, String counterName) {
        long startTime = System.nanoTime();
        long durationNano = 0;
        int nThreads = 200;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(nThreads);

        try (ExecutorService executorService = Executors.newFixedThreadPool(nThreads)){
            for (int i = 0; i < nThreads; i++) {
                executorService.submit(() -> {
                    try {
                        start.await();
                        for (int j=0; j < 50000; j++) {
                            counter.increment();
                        }
                        finish.countDown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            start.countDown();
            finish.await();
            long endTime = System.nanoTime();
            durationNano = endTime - startTime;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (counterName.equals("BrokenCounter")){
            assertNotEquals(nThreads * 50000, counter.getCount());
        }
        else
            assertEquals(nThreads * 50000, counter.getCount());
        if (!headerPrinted) {
            System.out.println("\n-----------------------------------------------------------------------");
            System.out.printf("| %-30s | %-15s | %-20s |\n", "Counter Implementation", "Final Count", "Execution Time (ms)");
            System.out.println("-----------------------------------------------------------------------");
            headerPrinted = true;
        }

        double durationMillis = durationNano / 1_000_000.0;

        System.out.printf(
                "| %-30s | %-15s | %-20s |\n",
                counterName,
                String.format("%,d", counter.getCount()),
                String.format("%,.4f ms", durationMillis)
        );
        System.out.println("-----------------------------------------------------------------------");
    }

    public static Stream<Arguments> counterImplementations(){
        return Stream.of(
                Arguments.of(new BrokenCounter(), "BrokenCounter"),
                Arguments.of(new SynchronizedCounter(), "Synchronized counter"),
                Arguments.of(new AtomicCounter(), "Atomic counter")
        );
    }
}