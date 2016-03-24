/*
 * Copyright (c) 2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.borabora;

import java.util.ArrayList;
import java.util.List;

final class ParserBuilderImpl implements ParserBuilder {

    private final Input input;
    private final List<SemanticTagProcessor> processors = new ArrayList<>();

    public ParserBuilderImpl(Input input) {
        this.input = input;
        this.processors.add(BuiltInSemanticTagProcessor.INSTANCE);
    }

    @Override
    public <V> ParserBuilder semanticTagProcessor(SemanticTagProcessor<V> processor) {
        processors.add(processor);
        return this;
    }

    @Override
    public Parser build() {
        return new ParserImpl(input, processors);
    }
}
