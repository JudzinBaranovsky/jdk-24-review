package org.bananalaba.jdk24;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Gatherers;
import org.junit.jupiter.api.Test;

public class JdkBuiltInGatherers {

    @Test
    void slidingWindow() {
        var input = List.of(1, 2, 3, 4, 5);
        var windowSize = 2;

        var output = input.stream()
            .gather(Gatherers.windowSliding(windowSize))
            .toList();

        var expected = List.of(
            List.of(1, 2),
            List.of(2, 3),
            List.of(3, 4),
            List.of(4, 5)
        );
        assertThat(expected).isEqualTo(output);
    }

    @Test
    void fixedWindow() {
        var input = List.of(1, 2, 3, 4, 5);
        var windowSize = 2;

        var output = input.stream()
            .gather(Gatherers.windowFixed(windowSize))
            .toList();

        var expected = List.of(
            List.of(1, 2),
            List.of(3, 4),
            List.of(5)
        );
        assertThat(expected).isEqualTo(output);
    }

    @Test
    void fold() {
        var input = List.of(1, 2, 3, 4, 5);

        var output = input.stream()
            .gather(Gatherers.fold(() -> 1, (a, b) -> a * b))
            .toList();

        var expected = List.of(120);
        assertThat(expected).isEqualTo(output);
    }

    @Test
    void mapConcurrentInTwoVirtualThreads() {
        var input = List.of(1, 2, 3, 4, 5);

        var output = input.stream()
            .gather(Gatherers.mapConcurrent(2, a -> a * a))
            .toList();

        var expected = List.of(1, 4, 9, 16, 25);
        assertThat(expected).isEqualTo(output);
    }

    @Test
    void scan() {
        var input = List.of(1, 2, 3, 4, 5);

        var output = input.stream()
            .gather(Gatherers.scan(() -> 1, (a, b) -> a * b))
            .toList();

        var expected = List.of(1, 2, 6, 24, 120);
        assertThat(expected).isEqualTo(output);
    }

}
