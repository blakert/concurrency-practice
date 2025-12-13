package jcip.ch5_building_blocks.pipeline;

import org.junit.jupiter.api.Test;

public class PipelineTest {
    @Test
    public void basicPipelineExecution() {
        // next steps: want to find a good way to share a thread pool across
        // 1. workers in a given pipeline
        // 2. different pipelines so that each pipeline doesn't deal with threadpool overhead.
        Pipeline<String, Integer> pipeline = Pipeline.builder()
                .withSource(new SourceStage<String>())
                .concurrentMap(String::trim)
                .concurrentMap(String::length)
                .build();
    }
}
