
package edu.univ.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger seq = new AtomicInteger(1000000);
    public static String nextId(){ return "U" + seq.getAndIncrement(); }
}
