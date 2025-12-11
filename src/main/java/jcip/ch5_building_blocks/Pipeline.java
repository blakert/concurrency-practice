package jcip.ch5_building_blocks;

import java.util.List;

public class Pipeline<I, O> {
    // pipeline will do what?
    public Pipeline() {
        // read lines from file
        List<PipelineStage> stages = List.of(
                new PipelineStage<>(),
                new PipelineStage<>(),
                new PipelineStage<Integer, Integer>());
        stages.forEach(PipelineStage::run);
        try {
            stages.getLast().awaitTermination();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
