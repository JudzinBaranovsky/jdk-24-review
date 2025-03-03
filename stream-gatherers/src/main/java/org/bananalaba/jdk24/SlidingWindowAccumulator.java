package org.bananalaba.jdk24;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class SlidingWindowAccumulator {

    private final int windowSize;
    @Getter
    private final List<List<Integer>> windows = new ArrayList<>();

    public SlidingWindowAccumulator(final int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("window size must be at least 1");
        }

        this.windowSize = windowSize;

        windows.add(new ArrayList<>());
    }

    public void add(final int item) {
        var currentWindow = windows.getLast();
        if (currentWindow.size() == windowSize) {
            currentWindow = new ArrayList<>(currentWindow.subList(windowSize - 1, windowSize));
            windows.add(currentWindow);
        }

        currentWindow.add(item);
    }

}
