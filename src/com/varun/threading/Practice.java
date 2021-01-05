package com.varun.threading;

import java.util.*;

class practice {

    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        rotateLeft(2, ints);
    }

    public static List<Integer> rotateLeft(int d, List<Integer> arr) {
        // Write your code here
        List<Integer> result = new ArrayList<>(arr.size());
        for (int i=0; i<arr.size(); i++) {
            int k = 0;
            int rotatedBydIdx=i;
            while(k<d) {
                rotatedBydIdx--;
                if (rotatedBydIdx == -1) rotatedBydIdx = arr.size() - 1;
                k++;
            }
            System.out.println("Rotated idx: " + rotatedBydIdx + "for id:" + i);
            result.add(rotatedBydIdx, arr.get(i));
        }
        return result;
    }
}