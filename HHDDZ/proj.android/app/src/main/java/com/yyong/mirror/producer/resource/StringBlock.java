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
package com.yyong.mirror.producer.resource;

import android.util.Pair;

import com.yyong.mirror.producer.resource.util.DataUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class StringBlock {
    private int flags;
    private int chunkSize;
    private List<String> mStringPool;

    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * Reads whole (including chunk type) string block from stream. Stream must
     * be at the chunk type.
     *
     * @param reader ExtDataInput
     * @return StringBlock
     * @throws IOException Parsing resources.arsc error
     */
    public static StringBlock read(ByteBuffer reader) throws IOException {
        DataUtil.skipCheckChunkTypeInt(reader, CHUNK_STRINGPOOL_TYPE, CHUNK_NULL_TYPE);
        int chunkSize = reader.getInt();

        // ResStringPool_header
        int stringCount = reader.getInt();
        int styleCount = reader.getInt();
        int flags = reader.getInt();
        int stringsOffset = reader.getInt();
        int stylesOffset = reader.getInt();

        StringBlock block = new StringBlock();
        block.flags = flags;
        block.chunkSize = chunkSize;
        block.m_isUTF8 = (flags & UTF8_FLAG) != 0;

        block.m_stringOffsets = DataUtil.readIntArray(reader, stringCount);

        if (styleCount != 0) {
            block.m_styleOffsets = DataUtil.readIntArray(reader, styleCount);
        } else {
            block.m_styleOffsets = new int[0];
        }

        int size = ((stylesOffset == 0) ? chunkSize : stylesOffset) - stringsOffset;
        block.m_strings = new byte[size];
        reader.get(block.m_strings);

        if (stylesOffset != 0) {
            size = (chunkSize - stylesOffset);
            block.m_styles = DataUtil.readIntArray(reader, size / 4);

            // read remaining bytes
            int remaining = size % 4;
            if (remaining >= 1) {
                while (remaining-- > 0) {
                    reader.get();
                }
            }
        } else {
            block.m_styles = new int[0];
        }
        block.mStringPool = block.parseStringPool();
        return block;
    }

    public StringBlock newStringBlock(Map<String, String> changed) throws IOException {
        List<String> strings = new ArrayList<>(mStringPool);
        for (String key : changed.keySet()) {
            int position = strings.indexOf(key);
            if (position != -1) {
                strings.set(position, changed.get(key));
            }
        }
        Pair<byte[], int[]> pair = convertCurrentStringPool(strings, this.m_isUTF8);
        StringBlock block = new StringBlock();
        block.flags = this.flags;
        block.m_isUTF8 = m_isUTF8;
        block.m_strings = pair.first;
        block.m_stringOffsets = pair.second;
        block.m_styleOffsets = this.m_styleOffsets;
        block.m_styles = this.m_styles;
        int size = this.chunkSize + block.m_strings.length - this.m_strings.length;
        int padding = size % 4;
        block.chunkSize = size + (padding == 0 ? padding : (4 - padding));
        block.mStringPool = strings;
        return block;
    }


    public void write(ByteBuffer buffer) throws IOException {
        byte[] bytes = this.m_strings;
        int[] offset = this.m_stringOffsets;
        buffer.putInt(CHUNK_STRINGPOOL_TYPE);
        buffer.putInt(chunkSize);
        buffer.putInt(m_stringOffsets.length);
        buffer.putInt(m_styleOffsets.length);
        buffer.putInt(flags);
        int strPosition = 28 + m_styleOffsets.length * 4 + m_stringOffsets.length * 4;
        int stylePosition = strPosition + bytes.length;
        buffer.putInt(strPosition);
        buffer.putInt(stylePosition);
        for (int strOffset : offset) {
            buffer.putInt(strOffset);
        }
        for (int strOffset : m_styleOffsets) {
            buffer.putInt(strOffset);
        }
        buffer.put(bytes);
        if (m_styleOffsets.length != 0) {
            for (int style : m_styles) {
                buffer.putInt(style);
            }
        }
        int count = chunkSize - stylePosition + m_styles.length * 4;
        System.out.println("add padding " + count + " " + chunkSize);
        DataUtil.writePad(buffer, count);
    }

    private static Pair<byte[], int[]> convertCurrentStringPool(List<String> pool, boolean utf8) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int[] offsets = new int[pool.size()];
        Charset charset = utf8 ? StandardCharsets.UTF_8 : StandardCharsets.UTF_16LE;
        for (int i = 0; i < pool.size(); i++) {
            offsets[i] = os.size();
            os.write(DataUtil.encodeString(pool.get(i), charset));
        }
        return Pair.create(os.toByteArray(), offsets);
    }

    private List<String> parseStringPool() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < m_stringOffsets.length; i++) {
            list.add(getString(i));
        }
        return list;
    }

    public List<String> getStringPool() {
        return mStringPool;
    }

    /**
     * Returns raw string (without any styling information) at specified index.
     *
     * @param index int
     * @return String
     */
    public String getString(int index) {
        if (index < 0 || m_stringOffsets == null || index >= m_stringOffsets.length) {
            return null;
        }
        int offset = m_stringOffsets[index];
        int length;

        int[] val;
        if (m_isUTF8) {
            val = getUtf8(m_strings, offset);
            offset = val[0];
        } else {
            val = getUtf16(m_strings, offset);
            offset += val[0];
        }
        length = val[1];
        return decodeString(offset, length);
    }


    /**
     * Finds index of the string. Returns -1 if the string was not found.
     *
     * @param string String to index location of
     * @return int (Returns -1 if not found)
     */
    public int find(String string) {
        if (string == null) {
            return -1;
        }
        for (int i = 0; i != m_stringOffsets.length; ++i) {
            int offset = m_stringOffsets[i];
            int length = getShort(m_strings, offset);
            if (length != string.length()) {
                continue;
            }
            int j = 0;
            for (; j != length; ++j) {
                offset += 2;
                if (string.charAt(j) != getShort(m_strings, offset)) {
                    break;
                }
            }
            if (j == length) {
                return i;
            }
        }
        return -1;
    }

    private StringBlock() {
    }

    String decodeString(int offset, int length) {
        try {
            final ByteBuffer wrappedBuffer = ByteBuffer.wrap(m_strings, offset, length);
            return (m_isUTF8 ? UTF8_DECODER : UTF16LE_DECODER).decode(wrappedBuffer).toString();
        } catch (CharacterCodingException ex) {
            if (!m_isUTF8) {
                LOGGER.warning("Failed to decode a string at offset " + offset + " of length " + length);
                return null;
            }
        }

        try {
            final ByteBuffer wrappedBufferRetry = ByteBuffer.wrap(m_strings, offset, length);
            // in some places, Android uses 3-byte UTF-8 sequences instead of 4-bytes.
            // If decoding failed, we try to use CESU-8 decoder, which is closer to what Android actually uses.
            return CESU8_DECODER.decode(wrappedBufferRetry).toString();
        } catch (CharacterCodingException e) {
            LOGGER.warning("Failed to decode a string with CESU-8 decoder.");
            return null;
        }
    }

    private static final int getShort(byte[] array, int offset) {
        return (array[offset + 1] & 0xff) << 8 | array[offset] & 0xff;
    }

    private static final int[] getUtf8(byte[] array, int offset) {
        int val = array[offset];
        int length;
        // We skip the utf16 length of the string
        if ((val & 0x80) != 0) {
            offset += 2;
        } else {
            offset += 1;
        }
        // And we read only the utf-8 encoded length of the string
        val = array[offset];
        offset += 1;
        if ((val & 0x80) != 0) {
            int low = (array[offset] & 0xFF);
            length = ((val & 0x7F) << 8) + low;
            offset += 1;
        } else {
            length = val;
        }
        return new int[]{offset, length};
    }


    private static final int[] getUtf16(byte[] array, int offset) {
        int val = ((array[offset + 1] & 0xFF) << 8 | array[offset] & 0xFF);

        if ((val & 0x8000) != 0) {
            int high = (array[offset + 3] & 0xFF) << 8;
            int low = (array[offset + 2] & 0xFF);
            int len_value = ((val & 0x7FFF) << 16) + (high + low);
            return new int[]{4, len_value * 2};

        }
        return new int[]{2, val * 2};
    }

    private int[] m_stringOffsets;
    private byte[] m_strings;
    private int[] m_styleOffsets;
    private int[] m_styles;
    private boolean m_isUTF8;

    private final CharsetDecoder UTF16LE_DECODER = Charset.forName("UTF-16LE").newDecoder();
    private final CharsetDecoder UTF8_DECODER = Charset.forName("UTF-8").newDecoder();
    private final CharsetDecoder CESU8_DECODER = Charset.forName("CESU8").newDecoder();
    private static final Logger LOGGER = Logger.getLogger(StringBlock.class.getName());

    // ResChunk_header = header.type (0x0001) + header.headerSize (0x001C)
    private static final int CHUNK_STRINGPOOL_TYPE = 0x001C0001;
    private static final int CHUNK_NULL_TYPE = 0x00000000;
    private static final int UTF8_FLAG = 0x00000100;
}
