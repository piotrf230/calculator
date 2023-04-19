package com.example.complexcalculator;

import java.util.ArrayList;

public class CircularBuffer<T> {
    private ArrayList<T> buffer;
    private int pos;
    private int backs;
    private int capacity;

    public CircularBuffer(int capacity) {
        buffer = new ArrayList<>();
        pos = -1;
        backs = 0;
        this.capacity = capacity;
    }

    public void add(T obj) {
        pos = ++pos % capacity;
        if (buffer.size() <= pos) buffer.add(obj);
        else buffer.set(pos, obj);
        backs = Math.min(++backs, capacity);
    }

    public T back() {
        if (backs == 0) return null;
        backs--;
        if (--pos < 0) pos += capacity;
        return buffer.get(pos % capacity);
    }

    public T peek() {
        return backs == 0 ? null : buffer.get(pos);
    }

    public T peek(int pos) {
        if (pos > backs) return null;
        return buffer.get((this.pos - pos) % capacity);
    }

    public int getCapacity() {
        return capacity;
    }
}
