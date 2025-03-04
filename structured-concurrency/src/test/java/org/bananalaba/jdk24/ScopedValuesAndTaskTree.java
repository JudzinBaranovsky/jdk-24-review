package org.bananalaba.jdk24;

import java.util.List;
import org.bananalaba.jdk24.tasktree.Task;
import org.bananalaba.jdk24.tasktree.TracingContext;
import org.junit.jupiter.api.Test;

public class ScopedValuesAndTaskTree {

    @Test
    void shouldRunTaskTreeWithTracing() {
        var subTask1 = new Task("subtask-1", () -> System.out.println("subtask-1 working"));
        var subTask2 = new Task("subtask-2", () -> System.out.println("subtask-2 working"));

        var rootTask = new Task("root", List.of(
            subTask1::run,
            subTask2::run
        ));

        TracingContext.trace("test-trace-id", rootTask::run);
    }

}
