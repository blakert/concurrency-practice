package jcip.ch2_thread_safety.conccurentCounter;

public interface Counter {
    void reset();
    int getCount();
    void increment();
}
