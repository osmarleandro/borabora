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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

final class TagProcessors {

    static Date readDateTime(Decoder stream, long index, long length, Collection<SemanticTagProcessor> processors) {
        short head = stream.transientUint8(index);
        int byteSize = ByteSizes.intByteSize(stream, head);
        String date = stream.readString(index + byteSize);
        return DateParser.parseDate(date, Locale.ENGLISH);
    }

    static BigInteger readUBigNum(Decoder stream, long index, long length, Collection<SemanticTagProcessor> processors) {
        short head = stream.transientUint8(index);
        int byteSize = ByteSizes.intByteSize(stream, head);
        String bigNum = stream.readString(index + byteSize);
        try {
            byte[] bytes = bigNum.getBytes("ASCII");
            return new BigInteger(1, bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("NBigNum could not be read", e);
        }
    }

    static BigInteger readNBigNum(Decoder stream, long index, long length, Collection<SemanticTagProcessor> processors) {
        return BigInteger.valueOf(-1).xor(readUBigNum(stream, index, length, processors));
    }

    static URI readURI(Decoder stream, long index, long length, Collection<SemanticTagProcessor> processors) {
        short head = stream.transientUint8(index);
        int byteSize = ByteSizes.intByteSize(stream, head);
        String uri = stream.readString(index + byteSize);
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI could not be read", e);
        }
    }

    static Number readTimestamp(Decoder stream, long index, long length, Collection<SemanticTagProcessor> processors) {
        ValueType valueType = ValueTypes.valueType(stream, index + 1);
        return stream.readNumber(valueType, index + 1);
    }

    static Value readEncCBOR(Decoder stream, long index, long length, Collection<SemanticTagProcessor> processors) {
        short tagHead = stream.transientUint8(index);
        int headByteSize = ByteSizes.intByteSize(stream, tagHead);

        index += headByteSize;
        short head = stream.transientUint8(index);
        MajorType majorType = MajorType.findMajorType(head);
        ValueType valueType = ValueTypes.valueType(stream, index);
        return new StreamValue(majorType, valueType, stream, index, length - headByteSize, processors);
    }
}