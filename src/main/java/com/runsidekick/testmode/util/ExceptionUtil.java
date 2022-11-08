package com.runsidekick.testmode.util;

/**
 * @author yasin.kalafat
 */
public final class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static <T> T sneakyThrow(Throwable t) {
        ExceptionUtil.<RuntimeException>sneakyThrowInternal(t);
        return (T) t;
    }

    private static <T extends Throwable> void sneakyThrowInternal(Throwable t) throws T {
        throw (T) t;
    }

}