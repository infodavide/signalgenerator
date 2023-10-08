package org.infodavid.signalgenerator.generator;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * The Class AbstractNumericGenerator.
 */
public abstract class AbstractNumericGenerator extends AbstractGenerator {

    /** The noise amplitude field. */
    private final JSpinner noiseAmplitudeField = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 0.5));

    /** The amplitude field. */
    private final JSpinner amplitudeField = new JSpinner(new SpinnerNumberModel(1.0, 1.0, 10000.0, 0.5));

    /** The offset field. */
    private final JSpinner offsetField = new JSpinner(new SpinnerNumberModel(0.0, -10000.0, 10000.0, 0.5));

    /**
     * Instantiates a new generator.
     * @param name the name
     */
    protected AbstractNumericGenerator(final String name) {
        super(name);
    }

    /**
     * Gets the amplitude.
     * @return the amplitudeField
     */
    public double getAmplitude() {
        return ((Number) amplitudeField.getValue()).doubleValue();
    }

    /**
     * Gets the maximum amplitude.
     * @return the maximumAmplitude
     */
    public double getMaximumAmplitude() {
        return ((Number) ((SpinnerNumberModel) amplitudeField.getModel()).getMaximum()).doubleValue();
    }

    /**
     * Gets the maximum noise amplitude.
     * @return the maximum noise amplitude
     */
    public double getMaximumNoiseAmplitude() {
        return ((Number) ((SpinnerNumberModel) noiseAmplitudeField.getModel()).getMaximum()).doubleValue();
    }

    /**
     * Gets the maximum offset.
     * @return the maximumOffset
     */
    public double getMaximumOffset() {
        return ((Number) ((SpinnerNumberModel) offsetField.getModel()).getMaximum()).doubleValue();
    }

    /**
     * Gets the minimum amplitude.
     * @return the minimumAmplitude
     */
    public double getMinimumAmplitude() {
        return ((Number) ((SpinnerNumberModel) amplitudeField.getModel()).getMinimum()).doubleValue();
    }

    /**
     * Gets the minimum noise amplitude.
     * @return the minimum noise amplitude
     */
    public double getMinimumNoiseAmplitude() {
        return ((Number) ((SpinnerNumberModel) noiseAmplitudeField.getModel()).getMinimum()).doubleValue();
    }

    /**
     * Gets the minimum offset.
     * @return the minimumOffset
     */
    public double getMinimumOffset() {
        return ((Number) ((SpinnerNumberModel) offsetField.getModel()).getMinimum()).doubleValue();
    }

    /**
     * Gets the noise amplitude.
     * @return the amplitudeField
     */
    public double getNoiseAmplitude() {
        return ((Number) noiseAmplitudeField.getValue()).doubleValue();
    }

    /**
     * Gets the offset.
     * @return the offsetField
     */
    public double getOffset() {
        return ((Number) offsetField.getValue()).doubleValue();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.signalgenerator.Generator#getValueClass()
     */
    @Override
    public Class<?> getValueClass() {
        return Double.class;
    }

    /**
     * Sets the amplitude.
     * @param value the new amplitude
     */
    public void setAmplitude(final double value) {
        if (value < getMinimumAmplitude() || value > getMaximumAmplitude()) {
            throw new IllegalArgumentException("Amplitude must be between " + getMinimumAmplitude() + " and " + getMaximumAmplitude());
        }

        amplitudeField.setValue(Double.valueOf(value));
    }

    /**
     * Sets the maximum amplitude.
     * @param value the maximum amplitude to set
     */
    public void setMaximumAmplitude(final double value) {
        if (value < getMinimumAmplitude()) {
            throw new IllegalArgumentException("Maximum amplitude must be greater than " + getMinimumAmplitude());
        }

        ((SpinnerNumberModel) amplitudeField.getModel()).setMaximum(Double.valueOf(value));
    }

    /**
     * Sets the maximum noise amplitude.
     * @param value the new maximum noise amplitude
     */
    public void setMaximumNoiseAmplitude(final double value) {
        if (value < getMinimumNoiseAmplitude()) {
            throw new IllegalArgumentException("Maximum noise amplitude must be greater than " + getMinimumNoiseAmplitude());
        }

        ((SpinnerNumberModel) noiseAmplitudeField.getModel()).setMaximum(Double.valueOf(value));
    }

    /**
     * Sets the maximum offset.
     * @param value the maximum offset to set
     */
    public void setMaximumOffset(final double value) {
        if (value < getMinimumOffset()) {
            throw new IllegalArgumentException("Maximum offset must be greater than " + getMinimumOffset());
        }

        ((SpinnerNumberModel) offsetField.getModel()).setMaximum(Double.valueOf(value));
    }

    /**
     * Sets the minimum amplitude.
     * @param value the minimum amplitude to set
     */
    public void setMinimumAmplitude(final double value) {
        if (value > getMaximumAmplitude()) {
            throw new IllegalArgumentException("Minimum amplitude must be lower than " + getMaximumAmplitude());
        }

        ((SpinnerNumberModel) amplitudeField.getModel()).setMinimum(Double.valueOf(value));
    }

    /**
     * Sets the minimum noise amplitude.
     * @param value the new minimum noise amplitude
     */
    public void setMinimumNoiseAmplitude(final double value) {
        if (value > getMaximumNoiseAmplitude()) {
            throw new IllegalArgumentException("Minimum noise amplitude must be lower than " + getMaximumNoiseAmplitude());
        }

        ((SpinnerNumberModel) noiseAmplitudeField.getModel()).setMinimum(Double.valueOf(value));
    }

    /**
     * Sets the minimum offset.
     * @param value the minimum offset to set
     */
    public void setMinimumOffset(final double value) {
        if (value > getMaximumOffset()) {
            throw new IllegalArgumentException("Minimum offset must be lower than " + getMaximumOffset());
        }

        ((SpinnerNumberModel) offsetField.getModel()).setMinimum(Double.valueOf(value));
    }

    /**
     * Sets the noise amplitude.
     * @param value the new maximum noise amplitude
     */
    public void setNoiseAmplitude(final int value) {
        if (value < getMinimumNoiseAmplitude() || value > getMaximumNoiseAmplitude()) {
            throw new IllegalArgumentException("Noise amplitude must be between " + getMinimumNoiseAmplitude() + " and " + getMaximumNoiseAmplitude());
        }

        noiseAmplitudeField.setValue(Double.valueOf(value));
    }

    /**
     * Sets the offset.
     * @param value the new offset
     */
    public void setOffset(final double value) {
        if (value < getMinimumOffset() || value > getMaximumOffset()) {
            throw new IllegalArgumentException("Offset must be between " + getMinimumOffset() + " and " + getMaximumOffset());
        }

        offsetField.setValue(Double.valueOf(value));
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.signalgenerator.AbstractGenerator#updateUi(javax.swing.JPanel, java.awt.GridBagConstraints)
     */
    @Override
    protected void updateUi(final JPanel panel, final GridBagConstraints constraints) {
        getLogger().debug("Updating UI...");
        // Amplitude
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel("Amplitude"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(amplitudeField, constraints);
        // Noise amplitude
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel("Noise amplitude"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(noiseAmplitudeField, constraints);
        // Offset
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel("Offset"), constraints);
        constraints.gridx = 1;
        constraints.insets = new Insets(2, 2, 2, 2);
        panel.add(offsetField, constraints);
        // Next ?
        constraints.gridx = 0;
        constraints.gridy++;
    }
}
