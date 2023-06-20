package com.programming.memento;

public interface Originator {
    Memento takeSnapshot();
    void restore(Memento memento);
}
