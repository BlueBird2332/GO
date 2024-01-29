package org.Bot;

public class MinStrategy implements MinMaxStrategy{
    @Override
    public boolean compare(int oldValue, int newValue) {
        return oldValue > newValue;
    }
}
