//: annotations/ExtractInterface.java
// APT-based annotation processing.
package org.sean.processor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtractInterface {
    public String value();
} ///:~
