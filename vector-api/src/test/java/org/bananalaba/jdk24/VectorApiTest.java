package org.bananalaba.jdk24;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class VectorApiTest {

    private final VectorMath math = new VectorMath();

    @Test
    void shouldAddTwoVectors() {
        var inputA = new int[] {1, 2, 3};
        var inputB = new int[] {4, 5, 6};

        var output = math.addVectorsWithoutMasking(inputA, inputB);

        assertThat(output).isEqualTo(new int[] {5, 7, 9});
    }

    @Test
    void shouldAddTwoVectorsFaster() {
        var inputA = new int[] {1, 2, 3};
        var inputB = new int[] {4, 5, 6};

        var output = math.addVectorsWithMasking(inputA, inputB);

        assertThat(output).isEqualTo(new int[] {5, 7, 9});
    }

}
