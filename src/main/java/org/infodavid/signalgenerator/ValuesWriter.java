package org.infodavid.signalgenerator;

import java.io.IOException;

/**
 * The Interface ValuesWriter.
 * @param <T> the generic type
 */
@FunctionalInterface
public interface ValuesWriter {

    /**
     * Write.
     * @param timestamp the timestamp
     * @param value     the value
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void write(long timestamp, Object value) throws IOException;
}
