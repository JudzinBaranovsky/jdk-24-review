package org.bananalaba.jdk24;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Gatherers;
import org.junit.jupiter.api.Test;

public class MotivationCases {

    @Test
    void firstTest() {
        var actual = List.of(1, 2, 3, 4, 5).stream()
            .gather(Gatherers.windowFixed(2))
            .toList();

        var expected = List.of(List.of(1, 2), List.of(3, 4), List.of(5));
        assertThat(actual).isEqualTo(expected);
    }

}
