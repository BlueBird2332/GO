package org.Bot;

public class MaxStrategy implements MinMaxStrategy{
    @Override
    public int compare(int a, int b) {
        return Integer.max(a, b);
    }
}
