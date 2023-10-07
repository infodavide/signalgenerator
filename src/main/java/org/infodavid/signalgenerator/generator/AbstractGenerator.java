package org.infodavid.signalgenerator.generator;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.infodavid.signalgenerator.Constants;
import org.infodavid.signalgenerator.ValuesWriter;
import org.slf4j.Logger;

/**
 * The Class AbstractGenerator.
 */
abstract class AbstractGenerator implements Generator {

    /** The frequency field. */
    private final JSpinner frequencyField = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 10.0, 0.1));

    /** The name. */
    private final String name;

    /**
     * Instantiates a new abstract generator.
     * @param name the name
     */
    protected AbstractGenerator(final String name) {
        this.name = name;
    }

    /**
     * Gets the frequency.
     * @return the frequency
     */
    public double getFrequency() {
        return ((Number) frequencyField.getValue()).doubleValue();
    }

    /**
     * Gets the maximum frequency.
     * @return the maximumFrequency
     */
    public double getMaximumFrequency() {
        return ((Number) ((SpinnerNumberModel) frequencyField.getModel()).getMaximum()).doubleValue();
    }

    /**
     * Gets the minimum frequency.
     * @return the minimumFrequency
     */
    public double getMinimumFrequency() {
        return ((Number) ((SpinnerNumberModel) frequencyField.getModel()).getMinimum()).doubleValue();
    }

    /**
     * Gets the name.
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the frequency.
     * @param value the new frequency
     */
    public void setFrequency(final double value) {
        if (value < getMinimumFrequency() || value > getMaximumFrequency()) {
            throw new IllegalArgumentException("Frequency must be between " + getMinimumFrequency() + " and " + getMaximumFrequency());
        }

        frequencyField.setValue(Double.valueOf(value));
    }

    /**
     * Sets the maximum frequency.
     * @param value the maximum frequency to set
     */
    public void setMaximumFrequency(final double value) {
        if (value < getMinimumFrequency()) {
            throw new IllegalArgumentException("Maximum frequency must be greater than " + getMinimumFrequency());
        }

        ((SpinnerNumberModel) frequencyField.getModel()).setMaximum(Double.valueOf(value));
    }

    /**
     * Sets the minimum frequency.
     * @param value the minimum frequency to set
     */
    public void setMinimumFrequency(final double value) {
        if (value > getMaximumFrequency()) {
            throw new IllegalArgumentException("Minimum frequency must be lower than " + getMaximumFrequency());
        }

        ((SpinnerNumberModel) frequencyField.getModel()).setMinimum(Double.valueOf(value));
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.signalgenerator.Generator#updateUi(javax.swing.JPanel)
     */
    @Override
    public void updateUi(final JPanel panel) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        // Frequency
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridwidth = 1;
        panel.add(new JLabel("Frequency"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(frequencyField, constraints);
        // Next ?
        constraints.gridx = 0;
        constraints.gridy++;
        updateUi(panel, constraints);
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected abstract Logger getLogger();

    /**
     * Update UI.
     * @param panel       the panel
     * @param constraints the constraints
     */
    protected abstract void updateUi(JPanel panel, GridBagConstraints constraints);

    /**
     * Validate.
     * @param start  the start
     * @param end    the end
     * @param step   the step
     * @param writer the writer
     */
    protected void validate(final long start, final long end, final long step, final ValuesWriter writer) {
        if (writer == null) {
            throw new IllegalStateException("Invalid writer");
        }

        if (start < 0) {
            throw new IllegalStateException("Start must be positive");
        }

        if (end < start) {
            throw new IllegalStateException("End must be greater than start (" + start + ')');
        }

        if (step < Constants.MINIMUM_STEP) {
            throw new IllegalStateException("Step must be greater than " + Constants.MINIMUM_STEP + "ms");
        }

        if (step > Constants.MAXIMUM_STEP) {
            throw new IllegalStateException("Step must be lower than " + Constants.MAXIMUM_STEP + "ms");
        }
    }
}
