package jcip.ch5_buuilding_blocks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Producer implements Runnable {
    BlockingQueue<String> workQueue;
    int startNum;
    int endNum;
    public Producer(BlockingQueue<String> workQueue, int startNum, int endNum) {
        this.workQueue = workQueue;
        this.startNum = startNum;
        this.endNum = endNum;
        assert endNum > startNum;
    }

    @Override
    public void run() {
        try {
            for (int i = startNum ; i < endNum; i++) {
                System.out.println("Producer thread: " + this + " sent item " + i);
                workQueue.put("item" + i);
            }
            System.out.println("Producer thread: " + this + " sent poison pill");
            workQueue.put(Orchestrator.POSION_PILL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    // make a producer thread which reads strings from a file.
    // make a consumer thread which capitalizes each string.

}