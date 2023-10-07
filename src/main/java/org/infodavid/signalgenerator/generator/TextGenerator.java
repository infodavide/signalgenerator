/**
 *
 */
package org.infodavid.signalgenerator.generator;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.signalgenerator.ValuesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TextGenerator.
 */
public class TextGenerator extends AbstractGenerator {

    /** The name. */
    public static final String NAME = "Text";

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TextGenerator.class);

    /** The maximum length field. */
    private final JSpinner maximumLengthField = new JSpinner(new SpinnerNumberModel(512, 1, 512, 10));

    /** The prefix field. */
    private final JTextField prefixField = new JTextField();

    /** The sequence field. */
    private final JCheckBox sequenceField = new JCheckBox();

    /** The suffix field. */
    private final JTextField suffixField = new JTextField();

    /**
     * Instantiates a new generator.
     */
    public TextGenerator() {
        super(NAME);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.signalgenerator.Generator#generate(long, long, long, org.infodavid.signalgenerator.ValuesWriter)
     */
    @SuppressWarnings("boxing")
    @Override
    public void generate(final long start, final long end, final long step, final ValuesWriter writer) throws IOException {
        validate(start, end, step, writer);
        final String prefix = getPrefix();
        final String suffix = getSuffix();
        final int maximumLength = getMaximumLength();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generating values from: {} to: {} using step: {}ms, prefix: {}, suffix: {} and max length: {}", start, end, step, prefix, suffix, maximumLength);
        }

        Supplier<String> supplier;

        if (isSequence()) {
            final AtomicLong sequence = new AtomicLong(0);
            supplier = () -> {
                final long v = sequence.accumulateAndGet(1, (a, b) -> {
                    final long s = a + b;

                    if (s >= Long.MAX_VALUE) {
                        return 1;
                    }

                    return s;
                });

                return String.valueOf(v);
            };

        } else {
            final Random random = new Random(System.currentTimeMillis());
            supplier = () -> String.valueOf(Math.abs(random.nextInt()));
        }

        final String pattern = StringUtils.defaultString(prefix) + "%s" + StringUtils.defaultString(suffix);
        long time = start;
        String v;

        if (LOGGER.isTraceEnabled()) {
            do {
                v = StringUtils.left(String.format(pattern, supplier.get()), maximumLength);
                LOGGER.trace("Writing {} -> {}", String.valueOf(time), v);
                writer.write(time, v);
                time += step;
            } while (time < end);
        } else {
            do {
                writer.write(time, StringUtils.left(String.format(pattern, supplier.get()), maximumLength));
                time += step;
            } while (time < end);
        }
    }

    /**
     * Gets the maximum length.
     * @return the maximum length
     */
    public int getMaximumLength() {
        return ((Number) maximumLengthField.getValue()).intValue();
    }

    /**
     * Gets the maximum maximum length.
     * @return the maximum maximum length
     */
    public int getMaximumMaximumLength() {
        return ((Number) ((SpinnerNumberModel) maximumLengthField.getModel()).getMaximum()).intValue();
    }

    /**
     * Gets the minimum maximum length.
     * @return the minimum maximum length
     */
    public int getMinimumMaximumLength() {
        return ((Number) ((SpinnerNumberModel) maximumLengthField.getModel()).getMinimum()).intValue();
    }

    /**
     * Gets the prefix.
     * @return the prefix
     */
    public String getPrefix() {
        return prefixField.getText();
    }

    /**
     * Gets the suffix.
     * @return the suffix
     */
    public String getSuffix() {
        return suffixField.getText();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.signalgenerator.Generator#getValueClass()
     */
    @Override
    public Class<?> getValueClass() {
        return String.class;
    }

    /**
     * Checks if is sequence.
     * @return true, if is sequence
     */
    public boolean isSequence() {
        return sequenceField.isSelected();
    }

    /**
     * Sets the maximum length.
     * @param value the new maximum length
     */
    public void setMaximumLength(final int value) {
        if (value < getMinimumMaximumLength() || value > getMaximumMaximumLength()) {
            throw new IllegalArgumentException("Maximum length must be between " + getMinimumMaximumLength() + " and " + getMaximumMaximumLength());
        }

        maximumLengthField.setValue(Integer.valueOf(value));
    }

    /**
     * Sets the maximum maximum length.
     * @param value the new maximum maximum length
     */
    public void setMaximumMaximumLength(final int value) {
        if (value < getMinimumMaximumLength()) {
            throw new IllegalArgumentException("Maximum maximum length must be greater than " + getMinimumMaximumLength());
        }

        ((SpinnerNumberModel) maximumLengthField.getModel()).setMaximum(Integer.valueOf(value));
    }

    /**
     * Sets the minimum maximum length.
     * @param value the new minimum maximum length
     */
    public void setMinimumMaximumLength(final int value) {
        if (value > getMaximumMaximumLength()) {
            throw new IllegalArgumentException("Minimum maximum length must be lower than " + getMaximumMaximumLength());
        }

        ((SpinnerNumberModel) maximumLengthField.getModel()).setMinimum(Integer.valueOf(value));
    }

    /**
     * Sets the prefix.
     * @param prefix the new prefix
     */
    public void setPrefix(final String prefix) {
        prefixField.setText(StringUtils.defaultString(prefix));
    }

    /**
     * Sets the sequence.
     * @param value the new sequence
     */
    public void setSequence(final boolean value) {
        sequenceField.setSelected(value);
    }

    /**
     * Sets the suffix field.
     * @param suffix the new suffix field
     */
    public void setSuffixField(final String suffix) {
        suffixField.setText(StringUtils.defaultString(suffix));
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.signalgenerator.AbstractGenerator#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.signalgenerator.AbstractGenerator#updateUi(javax.swing.JPanel, java.awt.GridBagConstraints)
     */
    @Override
    protected void updateUi(final JPanel panel, final GridBagConstraints constraints) {
        LOGGER.debug("Updating UI...");
        // Prefix
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel("Prefix"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(prefixField, constraints);
        // Suffix
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel("Suffix"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(suffixField, constraints);
        // Sequence
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel("Use sequence"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(sequenceField, constraints);
        // Maximum length
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel("Maximum length"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(maximumLengthField, constraints);
        // Next ?
        constraints.gridx = 0;
        constraints.gridy++;
    }
}
