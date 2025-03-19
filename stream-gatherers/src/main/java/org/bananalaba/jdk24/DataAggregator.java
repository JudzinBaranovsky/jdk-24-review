package org.bananalaba.jdk24;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import lombok.NonNull;

public class DataAggregator {

    // cons - very explicit
    public List<Double> slidingWindowAverages(@NonNull final List<Integer> input, final int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("window size must be > 0");
        }

        var output = new ArrayList<Double>();
        for (int i = 0; i < input.size() - windowSize + 1; i++) {
            var window = input.subList(i, i + windowSize);
            var average = window.stream().mapToInt(Integer::intValue).average();
            output.add(average.orElse(0.0));
        }

        return output;
    }

    // pros - more declarative
    // cons - inefficient (memory + 2 streams)
    public Stream<Double> slidingWindowAveragesStreamWithCollector(@NonNull Stream<Integer> input, final int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("window size must be > 0");
        }

        return input.collect(
                () -> new SlidingWindowAccumulator(windowSize),
                SlidingWindowAccumulator::add,
                (left, right) -> {throw new UnsupportedOperationException();}
            )
            .getWindows()
            .stream()
            .map(this::average);
    }

    // pros - declarative
    // pros - efficient
    public Stream<Double> slidingWindowAveragesStreamWithGatherer(@NonNull Stream<Integer> input, final int windowSize) {
        return input.gather(slidingWindow(windowSize))
            .map(this::average);
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
