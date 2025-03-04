package org.bananalaba.jdk24.tasktree;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class Task {

    @NonNull
    private final String name;
    @NonNull
    private final List<Runnable> subTasks;

    public Task(final String name, Runnable subTask) {
        this(name, List.of(subTask));
    }

    public void run() {
        log.info("task {} started, trace id {}", name, TracingContext.getTraceId());

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            subTasks.forEach(subTask -> scope.fork(() -> {
                subTask.run();
                return null;
            }));

            scope.join();
            scope.throwIfFailed();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("task {} failed, trace id {}", name, TracingContext.getTraceId(), e);
            throw new TaskInternalException("task failed", e);
        }

        log.info("task {} completed, trace id {}", name, TracingContext.getTraceId());
    }

}
