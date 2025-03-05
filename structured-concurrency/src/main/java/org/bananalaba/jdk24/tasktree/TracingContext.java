package org.bananalaba.jdk24.tasktree;

public class TracingContext {

    private static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();

    public static void trace(final String traceId, final Runnable runnable) {
        ScopedValue.where(TRACE_ID, traceId).run(runnable);
    }

    public static String getTraceId() {
        return TRACE_ID.get();
    }

}
