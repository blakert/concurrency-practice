package jcip.ch2_thread_safety;

public class SynchronizedCounter implements Counter {
    volatile int count = 0;

    public SynchronizedCounter() {

    }

    public synchronized void reset() {
        count = 0;
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized void increment() {
        count++;
    }
}
