/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.borabora.spi;

import com.noctarius.borabora.MajorType;
import com.noctarius.borabora.Value;
import com.noctarius.borabora.ValueType;
import com.noctarius.borabora.impl.DictionaryImpl;
import com.noctarius.borabora.spi.query.QueryContext;

import static com.noctarius.borabora.spi.io.Bytes.readUInt8;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The <tt>RelocatableStreamValue</tt> class implements a special version of a stream based
 * {@link com.noctarius.borabora.Value} type. The actual implementation can be used to prevent
 * a new <tt>Value</tt> instance per value. The relocated type cannot be returned to a user but
 * can be used for {@link java.util.function.Predicate} based searches in sequences or
 * dictionaries.
 */
public final class RelocatableStreamValue
        extends AbstractStreamValue {

    private QueryContext queryContext;
    private MajorType majorType;
    private ValueType valueType;
    private long offset;

    /**
     * Creates a new RelocatableStreamValue.
     */
    public RelocatableStreamValue() {
    }

    @Override
    public MajorType majorType() {
        return majorType;
    }

    @Override
    public ValueType valueType() {
        return valueType;
    }

    @Override
    public long offset() {
        return offset;
    }

    @Override
    public String toString() {
        return "RelocatableStreamValue{" + "valueType=" + valueType + ", offset=" + offset + ", value=" + byValueType() + '}';
    }

    @Override
    protected <T> T extractTag() {
        return queryContext().applyDecoder(offset(), majorType(), valueType());
    }

    /**
     * Relocates the current instance based on the given <tt>offset</tt>. The provided <tt>majorType</tt> and
     * <tt>valueType</tt> as well as the <tt>queryContext</tt> describe the actual data item and source for
     * the offset. On relocation, every previously known state of this instance is lost and previously accessible
     * elements are not available anymore.
     *
     * @param queryContext the current QueryContext
     * @param majorType    the current MajorType
     * @param valueType    the current ValueType
     * @param offset       the current offset
     * @throws NullPointerException     if one of majorType, valueType or queryContext is null
     * @throws IllegalArgumentException if offset is less than 0
     */
    public void relocate(QueryContext queryContext, MajorType majorType, ValueType valueType, long offset) {
        Objects.requireNonNull(queryContext, "queryContext must not be null");
        Objects.requireNonNull(majorType, "majorType must not be null");
        Objects.requireNonNull(valueType, "valueType must not be null");
        if (offset <= -1) {
            throw new IllegalArgumentException("No offset available for CBOR type, offset=" + offset);
        }

        this.queryContext = queryContext;
        this.majorType = majorType;
        this.valueType = valueType;
        this.offset = offset;
    }

    @Override
    public QueryContext queryContext() {
        return queryContext;
    }

	public long extracted(Predicate<Value> predicate, boolean findValue, DictionaryImpl dictionaryImpl) {
		for (long i = findValue ? 1 : 0; i < dictionaryImpl.size * 2; i = i + 2) {
	        long offset = dictionaryImpl.calculateArrayIndex(i);
	        short head = readUInt8(dictionaryImpl.input, offset);
	        MajorType majorType = MajorType.findMajorType(head);
	        ValueType valueType = dictionaryImpl.queryContext.valueType(offset);
	
	        relocate(dictionaryImpl.queryContext, majorType, valueType, offset);
	        if (predicate.test(this)) {
	            return offset;
	        }
	    }
	    return -1;
	}

}
