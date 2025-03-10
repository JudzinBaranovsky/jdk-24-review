package org.bananalaba.jdk24;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Fork(warmups = 1, value = 3)
@Measurement(iterations = 1)
public class SpringBootColdStartTest {

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void test(final Blackhole blackhole, final ApplicationState state) {
        var webApp = state.run();
        var result = webApp.getBean("messageController");
        blackhole.consume(result);
    }

    @State(Scope.Thread)
    public static class ApplicationState {

        private ConfigurableApplicationContext webApp;

        public ConfigurableApplicationContext run() {
            webApp = SpringApplication.run(MessagingApplication.class);
            return webApp;
        }

        @TearDown
        public void stop() {
            webApp.close();
        }

    }

}
