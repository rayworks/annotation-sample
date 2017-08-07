//: annotations/ExtractInterface.java
// APT-based annotation processing.
package org.sean.processor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ExtractInterface {
    String value();
} ///:~
