package org.bananalaba.jdk24;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

@Slf4j
public class StructuredConcurrencyCornerCases {

    @Test
    void shouldNotAllowPeerSubTaskInteraction() {
        assertThatThrownBy(this::runDependentTasks)
            .isInstanceOf(RuntimeException.class)
            .hasCauseExactlyInstanceOf(ExecutionException.class);
    }

    private void runDependentTasks() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var latch = new CountDownLatch(1);
            var subTask1 = scope.fork(() -> {
                latch.await();
                log.info("subtask-1 working");

                return "result 1";
            });

            var subTask2 = scope.fork(() -> {
                var dependencyResult = subTask1.get();
                latch.countDown();

                return "enriched " + dependencyResult;
            });

            scope.join();
            scope.throwIfFailed();

            log.info(subTask2.get());
        } catch (Exception e) {
            log.warn("failure", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldHandleSubTaskExceptionAndShutdown() {
        assertThatThrownBy(this::runWithShutdownPolicy)
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("subtask-1 failed");
    }

    private void runWithShutdownPolicy() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var subTask1 = scope.fork(() -> {
                Thread.sleep(1000);
                throw new RuntimeException("subtask-1 failed");
            });

            var latch = new CountDownLatch(1);
            var subTask2 = scope.fork(() -> {
                try {
                    latch.await();

                    log.info("subtask-2 working");

                    return "subtask-2 result";
                } catch (InterruptedException e) {
                    log.warn("subtask-2 interrupted", e);
                    throw e;
                }
            });

            scope.join();
            scope.throwIfFailed();

            log.info(subTask1.get() + " and " + subTask2.get());
        } catch (Exception e) {
            log.warn("failure", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldRunOnPlatformThread() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure("platform-scope", Thread.ofPlatform().factory())) {
            scope.fork(() -> {
                log.info("running in a platform thread is possible yet not recommended");
                return null;
            });

            scope.join();
            scope.throwIfFailed();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
