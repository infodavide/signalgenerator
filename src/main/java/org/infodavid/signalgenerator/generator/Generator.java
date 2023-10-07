package org.infodavid.signalgenerator.generator;

import java.io.IOException;

import javax.swing.JPanel;

import org.infodavid.signalgenerator.ValuesWriter;

/**
 * The Interface Generator.
 */
public interface Generator {

    /**
     * Generate.
     * @param start  the start time in milliseconds
     * @param end    the end time in milliseconds
     * @param step   the step in milliseconds
     * @param writer the writer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void generate(long start, long end, long step, ValuesWriter writer) throws IOException;

    /**
     * Gets the name.
     * @return the name
     */
    String getName();

    /**
     * Gets the value class.
     * @return the value class
     */
    Class<?> getValueClass();

    /**
     * Update UI by adding the component used to setup the generation of the signal.
     * @param panel the panel
     */
    void updateUi(JPanel panel);
}
