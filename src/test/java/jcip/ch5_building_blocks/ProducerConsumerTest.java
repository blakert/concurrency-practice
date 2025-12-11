package jcip.ch5_building_blocks;

import org.junit.jupiter.api.Test;

import java.security.Provider;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ProducerConsumerTest {
    @Test
    void testConsumerProcessesItemsAndPoisonPill() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
        queue.put("item1");
        queue.put("item2");
        queue.put(Producer.POISON_PILL);

        Consumer consumer = new Consumer(queue, String::toUpperCase);
        Thread t = new Thread(consumer);
        t.start();
        t.join(5000); // wait for consumer to finish

        assertEquals(2, consumer.numProcessed, "Consumer should process 2 items");
        // Queue should still have poison pill at the end
        assertEquals(Producer.POISON_PILL, queue.take());
    }

    @Test
    void testProducerPutsItemsAndPoisonPill() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
        Producer producer = new Producer(queue, 0, 3);

        Thread t = new Thread(producer);
        t.start();
        t.join(200);

        // Check that producer put the correct items
        assertEquals("item0", queue.take());
        assertEquals("item1", queue.take());
        assertEquals("item2", queue.take());

        // Poison pill should be last
        assertEquals(Producer.POISON_PILL, queue.take());
    }

    @Test
    void testOrchestratorPipelineProcessesAllItems() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
        int nConsumers = 2;
        int nItems = 15;

        ExecutorService executor = Executors.newFixedThreadPool(nConsumers + 1);

        Producer producer = new Producer(queue, 0, nItems);
        Consumer consumer1 = new Consumer(queue, String::toUpperCase);
        Consumer consumer2 = new Consumer(queue, String::toUpperCase);

        executor.submit(producer);
        executor.submit(consumer1);
        executor.submit(consumer2);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        int totalProcessed = consumer1.numProcessed + consumer2.numProcessed;
        assertEquals(nItems, totalProcessed, "All items should be processed by consumers");

        // Queue should be empty except for poison pill
        assertTrue(queue.isEmpty() || queue.peek().equals(Producer.POISON_PILL),
                "Queue should be empty or have poison pill at the end");
    }
}