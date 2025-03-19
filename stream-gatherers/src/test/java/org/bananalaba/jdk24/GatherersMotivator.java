package org.bananalaba.jdk24;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

public class GatherersMotivator {

    private final DataAggregator aggregator = new DataAggregator();

    @Test
    void slidingWindowAverageImperative() {
        var input = List.of(1, 2, 3, 4, 5);

        var output = aggregator.slidingWindowAverages(input, 2);

        var expected = List.of(1.5, 2.5, 3.5, 4.5);
        assertThat(expected).isEqualTo(output);
    }

    @Test
    void slidingWindowAverageStreamCollector() {
        var input = List.of(1, 2, 3, 4, 5);

        var output = aggregator.slidingWindowAveragesStreamWithCollector(input.stream(), 2).toList();

        var expected = List.of(1.5, 2.5, 3.5, 4.5);
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void slidingWindowAverageStreamGatherer() {
        var input = List.of(1, 2, 3, 4, 5);

        var output = aggregator.slidingWindowAveragesStreamWithGatherer(input.stream(), 2).toList();

        var expected = List.of(1.5, 2.5, 3.5, 4.5);
        assertThat(expected).isEqualTo(output);
    }

}
