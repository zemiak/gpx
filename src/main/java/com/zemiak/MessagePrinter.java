package com.zemiak;

public class MessagePrinter {
    public static void println(String template, Object... args) {
        System.out.println(String.format(template, args));
    }
}
