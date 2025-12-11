package jcip.ch5_building_blocks;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
/*
* Pipeline<Integer> pipe = Pipeline.builder()           // no type yet
    .withSource(new SourceStage<String>())            // StagesBuilder<String>
    .then(new PipelineStage<String, Double>())        // StagesBuilder<Double>
    .then(new PipelineStage<Double, Long>())          // StagesBuilder<Long>
    .then(new PipelineStage<Long, Integer>())         // StagesBuilder<Integer>
    .build();
* */


public class Pipeline<I, O> {
    private final SourceStage sourceStage;
    private final PipelineStage<?, O> finalStage;

    // pipeline will do what?
    private Pipeline(SourceStage sourceStage, List<PipelineStage<?,?>> stages) {

    }
    // can use staged builder to guarantee source is set first and only once.
    public class SourceBuilder{
        SourceStage sourceStage;
        public SourceBuilder() {}

        public void withSource(SourceStage sourceStage) {
            this.sourceStage = sourceStage;
            return new StagesBuilder(sourceStage)
        }
    }
    public class StagesBuilder{
        private final SourceStage sourceStage;
        private final List<PipelineStage<?,?>> stages;
        public StagesBuilder(SourceStage sourceStage) {
            this.sourceStage = sourceStage;
            stages = new ArrayList<>();
        }
        public StagesBuilder then(PipelineStage<?, ?> nextStage) {
            stages.add(nextStage);
            return this;
        }
    }
    public class Builder<I, O> {
        SourceStage sourceStage;
        List<PipelineStage<?, ?>> stages;
        public Builder() {
            return new SourceBuilder();
        }
        public Pipeline<I,O> build() {
            return new Pipeline<>(sourceStage, stages);
        }
    }
}
