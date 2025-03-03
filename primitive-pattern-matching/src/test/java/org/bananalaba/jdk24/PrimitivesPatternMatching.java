package org.bananalaba.jdk24;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PrimitivesPatternMatching {

    @Mock
    private KeyValueEngine engine;
    @InjectMocks
    private KeyValueStorage storage;

    @Test
    void shouldMatchByte() {
        var actual = PrimitiveMatcher.isByte(25.0);
        assertThat(actual).isTrue();
    }

    @Test
    void shouldStoreByte() {
        var input = new Parameter("p", 25.0);

        storage.save(input);

        verify(engine).putByte("p", (byte) 25);
    }

    @Test
    void shouldStoreDouble() {
        var input = new Parameter("p", 0.5);

        storage.save(input);

        verify(engine).putDouble("p", 0.5);
    }

    @ParameterizedTest
    @MethodSource("vmClassCases")
    void shouldMatchVmClass(int numberOfCpuCores, VmInstanceClass expected) {
        var actual = CpuClassifier.classify(numberOfCpuCores);
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> vmClassCases() {
        return Stream.of(
            Arguments.of(1, VmInstanceClass.SMALL),
            Arguments.of(5, VmInstanceClass.MEDIUM),
            Arguments.of(100, VmInstanceClass.LARGE)
        );
    }

}
