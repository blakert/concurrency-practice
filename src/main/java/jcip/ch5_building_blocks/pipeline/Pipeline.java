package jcip.ch5_building_blocks.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;


public class Pipeline<I,O> {
    private final SourceStage<I> sourceStage;
    private final List<PipelineStage<?, ?>> middleStages;
    private final PipelineStage<?, O> endStage;
    public static final int DEFAULT_CAPACITY = 5;
    public static final int DEFAULT_WORKERS = 1;

    private Pipeline(SourceStage<I> sourceStage, List<PipelineStage<?,?>> stages,
                     PipelineStage<?, O> endStage) {
        this.sourceStage = sourceStage;
        this.middleStages = stages;
        this.endStage = endStage;
    }

    public static Start builder() {
        return new Start();
    }

    public static final class Start {
        Start() {}

        <I> AfterStart<I> withStart(SourceStage<I> start) {
            BlockingQueue<I> initialQueue = new ArrayBlockingQueue<>(DEFAULT_CAPACITY);
            CountDownLatch doneLatch = new CountDownLatch(1);
            return new AfterStart<>(start, initialQueue, doneLatch);
        }
    }

    public static final class AfterStart<I> {
        private final SourceStage<I> start;
        private final BlockingQueue<I> currentQueue;
        private final CountDownLatch doneLatch;
        private AfterStart(SourceStage<I> start,
                           BlockingQueue<I> currentQueue,
                           CountDownLatch doneLatch) {
            this.start = start;
            this.currentQueue = currentQueue;
            this.doneLatch = doneLatch;
        }

        public <N> AfterStage<I, N> concurrentMap(Function<I, N> processor) {
            BlockingQueue<N> outputQueue = new ArrayBlockingQueue<>(DEFAULT_CAPACITY);
            int numWorkers = DEFAULT_WORKERS;
            CountDownLatch nextLatch = new CountDownLatch(numWorkers);

            PipelineStage<I, N> stage = new PipelineStage<>(
                    currentQueue,
                    outputQueue,
                    doneLatch,
                    nextLatch,
                    numWorkers,
                    processor
            );

            List<PipelineStage<?, ?>> stages = new ArrayList<>();
            stages.add(stage);

            return new AfterStage<>(start, stages, stage, outputQueue, nextLatch);
        }
    }

    public static final class AfterStage<I, T> {

        private final SourceStage<I> start;
        private final List<PipelineStage<?, ?>> stages;
        private final PipelineStage<?, T> lastStage;
        private final BlockingQueue<T> currentQueue;
        private final CountDownLatch doneLatch;
        private AfterStage(SourceStage<I> start,
                           List<PipelineStage<?, ?>> stages,
                           PipelineStage<?, T> lastStage,
                           BlockingQueue<T> currentQueue,
                           CountDownLatch doneLatch) {
            this.start = start;
            this.stages = stages;
            this.lastStage = lastStage;
            this.currentQueue = currentQueue;
            this.doneLatch = doneLatch;
        }

        public <N> AfterStage<I, N> concurrentMap(Function<T, N> processor) {
            int numWorkers = DEFAULT_WORKERS;
            BlockingQueue<T> inputQueue = this.currentQueue; // from previous stage
            BlockingQueue<N> outputQueue = new ArrayBlockingQueue<>(DEFAULT_CAPACITY);
            CountDownLatch upstreamLatch = this.doneLatch;   // previous stage's latch
            CountDownLatch doneLatch = new CountDownLatch(numWorkers);

            PipelineStage<T, N> stage = new PipelineStage<>(
                    inputQueue,
                    outputQueue,
                    upstreamLatch,
                    doneLatch,
                    numWorkers,
                    processor
            );

            stages.add(stage);

            return new AfterStage<>(start, stages, stage, outputQueue, doneLatch);
        }

        public Pipeline<I, T> build() {
            return new Pipeline<>(start, stages, lastStage);
        }

    }
    public static void main(String[] args) {
        Pipeline<Integer, List<Integer>> pipeline = Pipeline.builder()
                .withStart(new SourceStage<Integer>())
                .concurrentMap((i) -> Integer.toString(i),1)
                .concurrentMap(String::toUpperCase, 1)
                .concurrentMap(Long::parseLong, 1)
                .concurrentMap((l) -> List.of(1,2,3),1)
                .build();
    }
}

