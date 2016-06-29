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

import com.noctarius.borabora.spi.QueryContext;
import com.noctarius.borabora.spi.SelectStatementStrategy;
import com.noctarius.borabora.spi.SemanticTagProcessor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

final class QueryContextImpl
        implements QueryContext {

    // Queries are inherently thread-safe!
    private final Deque<Object> stack = new LinkedList<>();
    private final List<SemanticTagProcessor> semanticTagProcessors;
    private final SelectStatementStrategy selectStatementStrategy;
    private final Input input;

    QueryContextImpl(Input input, List<SemanticTagProcessor> semanticTagProcessors,
                     SelectStatementStrategy selectStatementStrategy) {

        this.input = input;
        this.semanticTagProcessors = semanticTagProcessors;
        this.selectStatementStrategy = selectStatementStrategy;
    }

    QueryContextImpl(Input input, QueryContextImpl queryContext) {
        this.input = input;
        this.semanticTagProcessors = queryContext.semanticTagProcessors;
        this.selectStatementStrategy = queryContext.selectStatementStrategy;
    }

    @Override
    public Input input() {
        return input;
    }

    @Override
    public <T> T applyProcessors(long offset, MajorType majorType) {
        SemanticTagProcessor<T> processor = findProcessor(offset);
        if (processor == null) {
            return null;
        }
        long length = Decoder.length(input, majorType, offset);
        return processor.process(offset, length, this);
    }

    @Override
    public <T> void queryStackPush(T element) {
        stack.addFirst(element);
    }

    @Override
    public <T> T queryStackPop() {
        return (T) stack.removeFirst();
    }

    @Override
    public <T> T queryStackPeek() {
        return (T) stack.peekFirst();
    }

    private <V> SemanticTagProcessor<V> findProcessor(long offset) {
        for (int i = 0; i < semanticTagProcessors.size(); i++) {
            SemanticTagProcessor<V> semanticTagProcessor = semanticTagProcessors.get(i);
            if (semanticTagProcessor.handles(input, offset)) {
                return semanticTagProcessor;
            }
        }
        return null;
    }

}