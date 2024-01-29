package org.Bot;

public class MinStrategy implements MinMaxStrategy{
    @Override
    public int compare(int a, int b) {
        return Integer.min(a,b);
    }
}
