package org.bananalaba.jdk24;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class LoggingProxyFactoryTest {

    @Mock
    private Logger logger;
    @InjectMocks
    private LoggingProxyFactory factory;

    @Test
    void shouldCreateProxy() {
        var proxy = factory.wrap(CalculatorService.class);

        var expected = proxy.add(1, 2);
        assertThat(expected).isEqualTo(3);

        verify(logger).info("add({}, {})", 1, 2);
    }

}
