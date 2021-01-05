package com.varun.threading.dataSharing.stackHeapRegions4;

public class StackRegionDebug1 {

    public static void main(String[] args) {
        int x = 1;
        int y = 2;
        int result = sum(x, y);
    }

    public static int sum(int a, int b) {
        int s = a + b;
        return s;
    }
}
