package org.bot;

public class MaxStrategy implements MinMaxStrategy{
    @Override
    public boolean compare(int oldValue, int newValue) {
        return oldValue < newValue;
    }
}
