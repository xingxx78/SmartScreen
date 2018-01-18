package com.wayne.www.waynelib.fdc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Think on 4/26/2016.
 */
public class IdDistributer {
    // id start with 1.
    private static AtomicInteger nextId = new AtomicInteger(0);

    public static int getAndConsumeId() {
        return nextId.getAndIncrement();
    }

    public static int getNextIdWithoutConsume() {
        return nextId.get();
    }
}
