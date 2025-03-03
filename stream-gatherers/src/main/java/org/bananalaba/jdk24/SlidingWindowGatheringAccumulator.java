package org.bananalaba.jdk24;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherer.Downstream;
import lombok.Getter;

public class SlidingWindowGatheringAccumulator {

    private final int windowSize;
    @Getter
    private List<Integer> currentWindow = new ArrayList<>();

    public SlidingWindowGatheringAccumulator(final int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("window size must be at least 1");
        }

        this.windowSize = windowSize;
    }

    public void add(final int item, final Downstream<? super List<Integer>> downstream) {
        if (currentWindow.size() == windowSize) {
            downstream.push(currentWindow);
            currentWindow = new ArrayList<>(currentWindow.subList(windowSize - 1, windowSize));
        }

        currentWindow.add(item);
    }

}
