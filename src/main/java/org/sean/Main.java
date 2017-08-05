package org.sean;

import org.sean.model.Multiplie.IMultiplier;
import org.sean.processor.TestAnnotation;

public class Main {
    @TestAnnotation(value = 5, what = "This is a test")
    public static String msg = "Hello world!";

    public static void main(String[] args) {
        System.out.println(String.format("Interface generated : %s", IMultiplier.class));
    }
}
