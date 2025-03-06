package org.bananalaba.jdk24;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class VectorApiTest {

    private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    @Test
    void shouldAddTwoVectors() {
        var inputA = new int[] {1, 2, 3};
        var inputB = new int[] {4, 5, 6};

        var output = new int[3];

        for (var i = 0; i < inputA.length; i += SPECIES.length()) {
            // if the input is not a multiple of the vector length, we need to mask
            // internally, this might mean padding the vector with zeros which is not very efficient
            var mask = SPECIES.indexInRange(i, inputA.length);

            var aVector = IntVector.fromArray(SPECIES, inputA, i, mask);
            var bVector = IntVector.fromArray(SPECIES, inputB, i, mask);

            var cVector = aVector.add(bVector);
            cVector.intoArray(output, i, mask);
        }

        assertThat(output).isEqualTo(new int[] {5, 7, 9});
    }

    @Test
    void shouldAddTwoVectorsFaster() {
        var inputA = new int[] {1, 2, 3};
        var inputB = new int[] {4, 5, 6};

        var output = new int[3];

        var i = 0;
        // as an alternative to masking, it's possible to apply vectorisation
        // only to the input part which ideally fits the SIMD vector lengths...
        for (i = 0; i < SPECIES.loopBound(inputA.length); i += SPECIES.length()) {
            var aVector = IntVector.fromArray(SPECIES, inputA, i);
            var bVector = IntVector.fromArray(SPECIES, inputB, i);

            var cVector = aVector.add(bVector);
            cVector.intoArray(output, i);
        }

        // ... and then calculate the remainder using plain Java (JIT will likely unwind this loop)
        for (; i < inputA.length; i++) {
            output[i] = inputA[i] + inputB[i];
        }

        assertThat(output).isEqualTo(new int[] {5, 7, 9});
    }

}
