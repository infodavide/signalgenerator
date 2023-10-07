package org.infodavid.signalgenerator;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * The Class Constants.
 */
public class Constants {

    /** The Constant DATETIME_FORMAT. */
    public static final FastDateFormat DATETIME_FORMAT = FastDateFormat.getInstance("YYYY-MM-DD HH:mm:ss.SSS");

    /** The Constant MAXIMUM_STEP. */
    public static final long MAXIMUM_STEP = 86400000;

    /** The Constant MINIMUM_STEP. */
    public static final byte MINIMUM_STEP = 25;

    /** The Constant PI * 2. */
    public static final double PI2 = Math.PI * 2;

    /**
     * Instantiates a new constants.
     */
    private Constants() {
        // noop
    }
}
