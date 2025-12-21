package com.audtream.desktop.util;

public class Logger {

    public static void printMsg(String message) {
        System.out.println("[AUDTREAM]: " + message);
    }

    public static void printMsg(double value) {
        System.out.println("[AUDTREAM]: " + value);
    }

    public static void printErr(String message) {
        System.err.println("[AUDTREAM]: "  +message);
    }

    public static void printErr(double value) {
        System.err.println("[AUDTREAM]: " + value);
    }
}

