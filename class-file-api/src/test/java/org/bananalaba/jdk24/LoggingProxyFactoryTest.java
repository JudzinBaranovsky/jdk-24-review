package org.bananalaba.jdk24;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

        var expectedLogArguments = new Object[] {"1.0", "2"};
        verify(logger).info("add({})", expectedLogArguments);
    }

}
