package org.infodavid.signalgenerator.configuration;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * The Class Configuration.
 */
@JacksonXmlRootElement(namespace = "urn:signalgenerator", localName = "configuration")
public class Configuration implements Serializable {

    /** The Constant DEFAULT_TITLE. */
    public static final String DEFAULT_TITLE = "Signal generator";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1506750481181331033L;

    /** The comment. */
    @JacksonXmlProperty(namespace = "urn:signalgenerator")
    private String comment;

    /** The title. */
    @JacksonXmlProperty(isAttribute = true)
    private String title;

    /**
     * Gets the comment.
     * @return the comment
     */
    public String getComment() {
        return StringUtils.defaultString(comment, "");
    }

    /**
     * Gets the title.
     * @return the title
     */
    public String getTitle() {
        return StringUtils.defaultString(title, DEFAULT_TITLE);
    }

    /**
     * Sets the comment.
     * @param comment the comment to set
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Sets the title.
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Validate.
     */
    public void validate() {
        if (StringUtils.isEmpty(title)) {
            title = DEFAULT_TITLE;
        }
    }
}
