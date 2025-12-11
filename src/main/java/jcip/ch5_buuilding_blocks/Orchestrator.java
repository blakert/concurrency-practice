package jcip.ch5_buuilding_blocks;

import java.util.PropertyPermission;
import java.util.concurrent.*;

public class Orchestrator {
    public static String POSION_PILL = "";
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> workQueue = new ArrayBlockingQueue<>(5, true);
        int nConsumers = 2;
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService executorService = Executors.newFixedThreadPool(3)){
            executorService.submit(new Producer(workQueue, 0, 20,
                    start));
            for (int i = 0; i < nConsumers; i++) {
                executorService.submit(new Consumer(workQueue, start));
            }
        }
        System.out.println("start latch launched");
        start.countDown();
    }

}
