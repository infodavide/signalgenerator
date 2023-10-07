package org.infodavid.signalgenerator.generator;

import java.io.IOException;
import java.util.Random;
import java.util.function.LongFunction;

import org.infodavid.signalgenerator.ValuesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SineGenerator.
 */
public class TriangleGenerator extends AbstractNumericGenerator {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TriangleGenerator.class);

    /** The name. */
    public static final String NAME = "Triangle";

    /**
     * Instantiates a new generator.
     */
    public TriangleGenerator() {
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
        final double amplitude = getAmplitude();
        final double noiseAmplitude = getNoiseAmplitude();
        final double offset = getOffset();
        final double frequency = getFrequency();
        final LongFunction<Double> f;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generating values from: {} to: {} using step: {}ms, amplitude: {}, noise amplitude: {}, frequency: {}, offset: {}", start, end, step, amplitude, noiseAmplitude, frequency, offset);
        }

        if (noiseAmplitude > 0) {
            final Random random = new Random(System.currentTimeMillis());
            f = t -> Double.valueOf(amplitude / (frequency / 2) * (frequency / 2 - Math.abs(t / 1000.0 % frequency - frequency / 2)) + offset + random.nextGaussian() * noiseAmplitude);
        } else {
            f = t -> Double.valueOf(amplitude / (frequency / 2) * (frequency / 2 - Math.abs(t / 1000.0 % frequency - frequency / 2)) + offset);
        }

        long time = start;
        Double v;

        if (LOGGER.isTraceEnabled()) {
            do {
                v = f.apply(time);
                LOGGER.trace("Writing {} -> {}", String.valueOf(time), v);
                writer.write(time, v);
                time += step;
            } while (time < end);
        } else {
            do {
                writer.write(time, f.apply(time));
                time += step;
            } while (time < end);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.signalgenerator.AbstractGenerator#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
