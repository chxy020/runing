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


/**
 *
 * @author <a href="mailto:onacit@gmail.com">Jin Kwon</a>
 * @param <T> byte target type parameter
 */
public abstract class AbstractByteOutput<T> implements ByteOutput {


    /**
     * Creates a new instance wrapping specified byte target.
     *
     * @param target the underlying byte target. or {@code null} if it is
     * intended to be lazily initialized and set.
     */
    public AbstractByteOutput(final T target) {

        super();

        this.target = target;
    }


    /**
     * Returns the current value of {@link #target}.
     *
     * @return the current value of {@link #target}.
     */
    public T getTarget() {

        return target;
    }


    /**
     * Replaces the value of {@link #target} with given.
     *
     * @param target new value for {@link #target}.
     */
    public void setTarget(final T target) {

        this.target = target;
    }


    /**
     * The underlying byte target.
     *
     * @see #getTarget()
     * @see #setTarget(java.lang.Object)
     */
    protected T target;


}

