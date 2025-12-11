package jcip.ch5_building_blocks;

import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    public static String POISON_PILL = "";
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
            workQueue.put(Producer.POISON_PILL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    // make a producer thread which reads strings from a file.
    // make a consumer thread which capitalizes each string.

}