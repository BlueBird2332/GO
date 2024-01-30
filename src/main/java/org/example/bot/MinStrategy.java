package org.example.bot;

public class MinStrategy implements MinMaxStrategy{
    @Override
    public boolean compare(int oldValue, int newValue) {
        return oldValue > newValue;
    }
}
