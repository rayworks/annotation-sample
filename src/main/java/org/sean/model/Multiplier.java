//: annotations/Multiplier.java
// APT-based annotation processing.
package org.sean.model;

import org.sean.processor.ExtractInterface;

@ExtractInterface("IMultiplier")
public class Multiplier {
    public int multiply(int x, int y) {
        int total = 0;
        for (int i = 0; i < x; i++)
            total = add(total, y);
        return total;
    }

    private int add(int x, int y) {
        return x + y;
    }
}
