package jcip.ch2_thread_safety;

public class BrokenCounter implements Counter{
    int count = 0;
    public BrokenCounter() {

    }

    public void reset() {
        count = 0;
    }

    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
