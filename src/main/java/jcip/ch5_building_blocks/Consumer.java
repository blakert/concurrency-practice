package jcip.ch5_building_blocks;

import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public class Consumer implements Runnable {
    int numProcessed;
    BlockingQueue<String> workQueue;
    Function<String,String> stringProcessor;
    public Consumer(BlockingQueue<String> workQueue, Function<String,String> stringProcessor) {
        this.workQueue = workQueue;
        numProcessed = 0;
        this.stringProcessor = stringProcessor;
    }

    @Override
    public void run() {
        while(true) {
            try {
                String target = workQueue.take();
                if (target.equals(Producer.POISON_PILL)) {
                    workQueue.put(Producer.POISON_PILL);
                    System.out.println(this + "poison pill recvd and propagated.");
                    System.out.println(this + "processed: " + numProcessed);
                    break;
                }
                numProcessed++;
                System.out.println("Consumed on" + this + " string: "
                        + stringProcessor.apply(target));
                Thread.sleep(1000); // using the string
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // bubble interrupt up.
            }
        }
    }
}
