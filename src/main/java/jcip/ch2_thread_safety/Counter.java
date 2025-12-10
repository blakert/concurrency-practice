package jcip.ch2_thread_safety;

public interface Counter {
    void reset();
    int getCount();
    void increment();
}
