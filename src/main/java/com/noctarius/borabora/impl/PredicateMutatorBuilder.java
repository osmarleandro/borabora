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
package com.noctarius.borabora.impl;

import com.noctarius.borabora.Value;
import com.noctarius.borabora.builder.encoder.ValueBuilder;
import com.noctarius.borabora.impl.transformation.PredicateMutator;
import com.noctarius.borabora.impl.transformation.SkipTransformation;
import com.noctarius.borabora.spi.transformation.ChainingMutationBuilder;
import com.noctarius.borabora.spi.transformation.Mutator;
import com.noctarius.borabora.spi.transformation.MutatorBuilder;
import com.noctarius.borabora.spi.transformation.Transformation;
import com.noctarius.borabora.spi.transformation.TransformationBuilder;

import java.util.List;
import java.util.function.Predicate;

class PredicateMutatorBuilder
        implements MutatorBuilder{

    private final ChainingMutationBuilder parentBuilder;
    private final Predicate<Value> predicate;
    private final List<Mutator> mutators;

    PredicateMutatorBuilder(ChainingMutationBuilder parentBuilder, Predicate<Value> predicate, List<Mutator> mutators) {
        this.parentBuilder = parentBuilder;
        this.predicate = predicate;
        this.mutators = mutators;
    }

    @Override
    public ChainingMutationBuilder skip() {
        mutators.add(new PredicateMutator(predicate, SkipTransformation.SKIP));
        return parentBuilder;
    }

    @Override
    public ValueBuilder<TransformationBuilder> replaceWith() {
        return TransformationBuilderImpl.newTransformationBuilder(this::createMutator, parentBuilder, mutators);
    }

    @Override
    public ChainingMutationBuilder transformWith(Transformation transformation) {
        mutators.add(new PredicateMutator<>(predicate, transformation));
        return parentBuilder;
    }

    private Mutator createMutator(Transformation transformation) {
        return new PredicateMutator<>(predicate, transformation);
    }
}