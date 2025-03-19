package org.bananalaba.jdk24;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Fork(value = 1, warmups = 1, jvmArgs = {"--enable-preview", "--add-modules", "jdk.incubator.vector"})
@Warmup(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations =  1, time = 30, timeUnit = TimeUnit.SECONDS)
public class VectorMathBenchmark {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(jvmArgsAppend = "-XX:-UseSuperWord")
    public void testAddUsingPlainLoop(final TestContext context, final Blackhole blackhole) {
        var a = context.getA();
        var b = context.getB();

        var output = context.getMath().addUsingPlainLoop(a, b);
        blackhole.consume(output);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testAddVectorsWithoutMasking(final TestContext context, final Blackhole blackhole) {
        var a = context.getA();
        var b = context.getB();

        var output = context.getMath().addVectorsWithoutMasking(a, b);
        blackhole.consume(output);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testAddVectorsWithMasking(final TestContext context, final Blackhole blackhole) {
        var a = context.getA();
        var b = context.getB();

        var output = context.getMath().addVectorsWithMasking(a, b);
        blackhole.consume(output);
    }

    @State(Scope.Thread)
    public static class TestContext {

        private final int[] a;
        private final int[] b;

        private final VectorMath math = new VectorMath();

        public TestContext() {
            a = generateArray(10_313);
            b = generateArray(10_313);
        }

        private static int[] generateArray(final int size) {
            var random = new Random();
            var array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt();
            }

            return array;
        }

        public int[] getA() {
            return a;
        }

        public int[] getB() {
            return b;
        }

        public VectorMath getMath() {
            return math;
        }

    }

}
