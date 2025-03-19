package org.bananalaba.jdk24;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import lombok.NonNull;

public class VectorMath {

    private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    public int[] addUsingPlainLoop(@NonNull final int[] a, @NonNull final int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("the input vectors must have same size");
        }

        var output = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            output[i] = a[i] + b[i];
        }

        return output;
    }

    public int[] addVectorsWithMasking(@NonNull final int[] a, @NonNull final int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("the input vectors must have same size");
        }

        var output = new int[a.length];

        for (var i = 0; i < a.length; i += SPECIES.length()) {
            // if the input is not a multiple of the vector length, we need to mask
            // internally, this might mean padding the vector with zeros which is not very efficient
            var mask = SPECIES.indexInRange(i, a.length);

            var aVector = IntVector.fromArray(SPECIES, a, i, mask);
            var bVector = IntVector.fromArray(SPECIES, b, i, mask);

            var cVector = aVector.add(bVector);
            cVector.intoArray(output, i, mask);
        }

        return output;
    }

    public int[] addVectorsWithoutMasking(@NonNull final int[] a, @NonNull final int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("the input vectors must have same size");
        }

        var output = new int[a.length];

        var i = 0;
        // as an alternative to masking, it's possible to apply vectorisation
        // only to the input part which ideally fits the SIMD vector lengths...
        for (i = 0; i < SPECIES.loopBound(a.length); i += SPECIES.length()) {
            var aVector = IntVector.fromArray(SPECIES, a, i);
            var bVector = IntVector.fromArray(SPECIES, b, i);

            var cVector = aVector.add(bVector);
            cVector.intoArray(output, i);
        }

        // ... and then calculate the remainder using plain Java (JIT will likely unwind this loop)
        for (; i < a.length; i++) {
            output[i] = a[i] + b[i];
        }

        return output;
    }

}
