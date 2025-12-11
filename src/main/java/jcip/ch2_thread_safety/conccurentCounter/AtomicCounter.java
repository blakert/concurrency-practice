package jcip.ch2_thread_safety.conccurentCounter;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter implements Counter{
    AtomicInteger count;
    public AtomicCounter() {
        count = new AtomicInteger();
    }
    @Override
    public void reset() {
       count.set(0);
    }

    @Override
    public int getCount() {
        return count.get();
    }

    @Override
    public void increment() {
        count.incrementAndGet();
    }
}
