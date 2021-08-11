/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.excean.mirror.producer.resource;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.logging.Logger;


public class ARSCDecoder {
    private int size;
    private int stringBlockOffset;
    private int stringBlockLength;
    private int packageCount;

    private final ByteBuffer mReader;
    private Header mHeader;
    private StringBlock mTableStrings;

    public ARSCDecoder(ByteBuffer buffer) {
        mReader = buffer;
        size = buffer.limit();
    }

    public ByteBuffer write(Map<String, String> map) throws IOException {
        mReader.position(0);
        StringBlock block = mTableStrings.newStringBlock(map);
        int offset = block.getChunkSize() - mTableStrings.getChunkSize();
        Header header = mHeader.newHeader(mHeader.chunkSize + offset);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[header.chunkSize]).order(ByteOrder.LITTLE_ENDIAN);
        header.write(buffer);
        buffer.putInt(packageCount);
        block.write(buffer);
        writeExceptStringBlock(buffer);
        return buffer;
    }

    private void writeExceptStringBlock(ByteBuffer buffer) {
        int offset = stringBlockOffset + stringBlockLength;
        mReader.position(offset);
        buffer.put(mReader);
    }

    public void readStringBlock() throws IOException {
        mReader.position(0);
        nextChunkCheckType();
        packageCount = mReader.getInt();
        stringBlockOffset = mReader.position();
        mTableStrings = StringBlock.read(mReader);
        stringBlockLength = mReader.position() - stringBlockOffset;
    }


    private void nextChunk() throws IOException {
        mHeader = Header.read(mReader);
    }

    private void checkChunkType() throws IOException {
        if (mHeader.type != (int) Header.TYPE_TABLE) {
            throw new IOException(String.format("Invalid chunk type: expected=0x%08x, got=0x%08x",
                    (int) Header.TYPE_TABLE, mHeader.type));
        }
    }

    private void nextChunkCheckType() throws IOException {
        nextChunk();
        checkChunkType();
    }

    public static class Header {
        public final short type;
        public final int headerSize;
        public final int chunkSize;
        public final int startPosition;
        public final int endPosition;

        public Header(short type, int headerSize, int chunkSize, int headerStart) {
            this.type = type;
            this.headerSize = headerSize;
            this.chunkSize = chunkSize;
            this.startPosition = headerStart;
            this.endPosition = headerStart + chunkSize;
        }

        public void write(ByteBuffer buffer) {
            buffer.putShort(type);
            buffer.putShort((short) headerSize);
            buffer.putInt(chunkSize);
        }

        public Header newHeader(int size) {
            return new Header(type, headerSize, size, startPosition);
        }

        public static Header read(ByteBuffer in) throws IOException {
            short type;
            int start = in.position();
            try {
                type = in.getShort();
            } catch (BufferUnderflowException ex) {
                return new Header(TYPE_NONE, 0, 0, in.position());
            }
            return new Header(type, in.getShort(), in.getInt(), start);
        }

        public final static short TYPE_NONE = -1, TYPE_TABLE = 0x0002,
                TYPE_PACKAGE = 0x0200, TYPE_TYPE = 0x0201, TYPE_SPEC_TYPE = 0x0202, TYPE_LIBRARY = 0x0203;
    }

    public static class FlagsOffset {
        public final int offset;
        public final int count;

        public FlagsOffset(int offset, int count) {
            this.offset = offset;
            this.count = count;
        }
    }


    private static final Logger LOGGER = Logger.getLogger(ARSCDecoder.class.getName());
    private static final int KNOWN_CONFIG_BYTES = 56;


}
