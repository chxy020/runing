/*
 * Copyright 2013 <a href="mailto:onacit@gmail.com">Jin Kwon</a>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package net.yaopao.engine.manager.binaryIO;


import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * A {@link ByteInput} implementation for {@link ByteBuffer}s.
 */
public class BufferInput extends AbstractByteInput<ByteBuffer> {


    /**
     * Creates a new instance built on top of the specified byte buffer.
     *
     * @param source {@inheritDoc}
     */
    public BufferInput(final ByteBuffer source) {

        super(source);
    }


    /**
     * {@inheritDoc} The {@code readUnsignedByte()} method of {@code ByteReader}
     * class calls {@link ByteBuffer#get()} on {@link #source} and returns the
     * result.
     *
     * @return {@inheritDoc }
     *
     * @throws IllegalStateException {@inheritDoc}
     * @throws IOException {@inheritDoc}
     *
     * @see ByteBuffer#get()
     * @see #source
     * @see #getSource()
     * @see #setSource(java.lang.Object)
     */
    @Override
    public int readUnsignedByte() throws IOException {

        if (source == null) {
            throw new IllegalStateException("#source is currently null");
        }

        return source.get() & 0xFF;
    }


}

