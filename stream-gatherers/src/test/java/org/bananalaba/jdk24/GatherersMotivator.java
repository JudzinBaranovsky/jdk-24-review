package org.bananalaba.jdk24;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherer;
import org.junit.jupiter.api.Test;

public class GatherersMotivator {

    @Test
    // cons - very explicit
    void slidingWindowAverageImperative() {
        var input = List.of(1, 2, 3, 4, 5);
        var windowSize = 2;

        var output = new ArrayList<Double>();
        for (int i = 0; i < input.size() - windowSize + 1; i++) {
            var window = input.subList(i, i + windowSize);
            var average = window.stream().mapToInt(Integer::intValue).average();
            output.add(average.orElse(0.0));
        }

        var expected = List.of(1.5, 2.5, 3.5, 4.5);
        assertThat(expected).isEqualTo(output);
    }

    @Test
    // pros - more declarative
    // cons - inefficient (memory + 2 streams)
    void slidingWindowAverageStreamCollector() {
        var input = List.of(1, 2, 3, 4, 5);
        var windowSize = 2;

        var output = input.stream()
            .collect(
                () -> new SlidingWindowAccumulator(windowSize),
                SlidingWindowAccumulator::add,
                (left, right) -> {throw new UnsupportedOperationException();}
            )
            .getWindows()
            .stream()
            .map(this::average)
            .toList();

        var expected = List.of(1.5, 2.5, 3.5, 4.5);
        assertThat(output).isEqualTo(expected);
    }

    @Test
    // pros - declarative
    // pros - efficient
    void slidingWindowAverageStreamGatherer() {
        var input = List.of(1, 2, 3, 4, 5);
        var windowSize = 2;

        var output = input.stream()
            .gather(slidingWindow(windowSize))
            .map(this::average)
            .toList();

        var expected = List.of(1.5, 2.5, 3.5, 4.5);
        assertThat(expected).isEqualTo(output);
    }

    private Gatherer<Integer, SlidingWindowGatheringAccumulator, List<Integer>> slidingWindow(final int windowSize) {
        return Gatherer.ofSequential(
            () -> new SlidingWindowGatheringAccumulator(windowSize),
            (accumulator, item, downstream) -> {
                accumulator.add(item, downstream);
                return true;
            },
            (accumulator, downstream) -> downstream.push(accumulator.getCurrentWindow())
        );
    }

    double average(final List<Integer> window) {
        return window.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

}
