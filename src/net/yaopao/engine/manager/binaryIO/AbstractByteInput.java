

package net.yaopao.engine.manager.binaryIO;


/**
 *
 * @param <T> underlying byte source type parameter
 */
public abstract class AbstractByteInput<T> implements ByteInput {


    /**
     * Creates a new instance built on top of the specified underlying byte
     * source.
     *
     * @param source the underlying byte source to be assigned to the field
     * {@link #source} for later use, or {@code null} if this instance is
     * intended to be created without an underlying byte source.
     */
    public AbstractByteInput(final T source) {

        super();

        this.source = source;
    }


    /**
     * Returns the current value of {@link #source}.
     *
     * @return the current value of {@link #source}.
     */
    public T getSource() {

        return source;
    }


    /**
     * Replaces the value of {@link #source} with given.
     *
     * @param source new value for {@link #source}.
     */
    public void setSource(final T source) {

        this.source = source;
    }


    /**
     * The underlying byte source.
     *
     * @see #getSource()
     * @see #setSource(java.lang.Object)
     */
    protected T source;


}

