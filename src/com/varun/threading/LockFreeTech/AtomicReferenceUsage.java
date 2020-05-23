package com.varun.threading.LockFreeTech;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceUsage {

    public static void main(String[] args) {
        String oldName = "old name";
        String newName = "new name";
        AtomicReference<String> atomicRef = new AtomicReference<>(oldName);
        oldName = "Unexpected Value";
        if (atomicRef.compareAndSet(oldName, newName)) {
            System.out.println("Atomic ref value: " + atomicRef.get());
        } else {
            System.out.println("Atomic ref value: " + atomicRef.get());
        }
    }


}
