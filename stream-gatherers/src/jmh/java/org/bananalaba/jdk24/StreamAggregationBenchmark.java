package org.bananalaba.jdk24;

import java.util.Random;
import java.util.stream.Stream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@Fork(warmups = 0, jvmArgs = {"--enable-preview", "-Xms64M", "-Xmx64M"})
@Measurement(iterations = 1)
public class StreamAggregationBenchmark {

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void runSlidingWindowAveragesWithCollector(final TestContext context, final Blackhole blackhole) {
        var input = context.generateInput(1_000_000);
        var output = context.getAggregator().slidingWindowAveragesStreamWithCollector(input, 3);
        blackhole.consume(output.toList());
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void runSlidingWindowAveragesWithGatherer(final TestContext context, final Blackhole blackhole) {
        var input = context.generateInput(1_000_000);
        var output = context.getAggregator().slidingWindowAveragesStreamWithGatherer(input, 3);
        blackhole.consume(output.toList());
    }

    @State(Scope.Thread)
    public static class TestContext {

        private final DataAggregator aggregator = new DataAggregator();

        public Stream<Integer> generateInput(final int limit) {
            var random = new Random();
            return Stream.generate(random::nextInt).limit(limit);
        }

        public DataAggregator getAggregator() {
            return aggregator;
        }

    }

}
