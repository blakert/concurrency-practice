package jcip.ch5_buuilding_blocks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Consumer implements Runnable {
    int numProcessed;
    BlockingQueue<String> workQueue;
    CountDownLatch startLatch;
    public Consumer(BlockingQueue<String> workQueue, CountDownLatch startLatch) {
        this.workQueue = workQueue;
        numProcessed = 0;
        this.startLatch = startLatch;
    }

    @Override
    public void run() {
        while(true) {
            try {
                startLatch.await();
                String target = workQueue.take();
                if (target.equals(Orchestrator.POSION_PILL)) {
                    workQueue.put(Orchestrator.POSION_PILL);
                    System.out.println(this + "poison pill recvd and propagated.");
                    System.out.println(this + "processed: " + numProcessed);
                    break;
                }
                numProcessed++;
                System.out.println("Consumed on" + this + " string: "
                        + target.toUpperCase());
                Thread.sleep(1000); // using the string
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // bubble interrupt up.
            }
        }
    }
}
