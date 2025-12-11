package jcip.ch5_building_blocks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Worker<In,Out> implements Runnable{
    private final BlockingQueue<In> inputQueue;
    private final BlockingQueue<Out> outputQueue;
    private final Function<In, Out> processor;
    private final CountDownLatch upstreamDoneLatch;
    private final CountDownLatch doneLatch;
    public Worker(BlockingQueue<In> inputQueue, BlockingQueue<Out> outputQueue,
            Function<In, Out> processor, CountDownLatch upstreamDoneLatch,
                  CountDownLatch doneLatch) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.processor = processor;
        this.upstreamDoneLatch = upstreamDoneLatch;
        this.doneLatch = doneLatch;
    }

    @Override
    public void run() {
        // should read from input queue until upstream is done and queue is empty.
        while(upstreamDoneLatch.getCount() > 0 || !inputQueue.isEmpty()) {
            try {
                In input = inputQueue.poll(2, TimeUnit.SECONDS);
                if (input == null)
                    continue;
                Out output = processor.apply(input);
                System.out.println(this + "data processed: " + input);
                boolean enqueued = false;
                while (!enqueued) // retry until possible to enqueue. could hang.
                    enqueued = outputQueue.offer(output, 2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        doneLatch.countDown();
    }
}
