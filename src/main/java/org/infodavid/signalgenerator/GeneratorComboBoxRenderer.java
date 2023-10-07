package org.infodavid.signalgenerator;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.infodavid.signalgenerator.generator.Generator;

/**
 * The Class GeneratorComboBoxRenderer.
 */
class GeneratorComboBoxRenderer extends BasicComboBoxRenderer {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 113375511742351004L;

    /*
     * (non-javadoc)
     * @see javax.swing.plaf.basic.BasicComboBoxRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        final Generator generator = (Generator) value;

        if (generator == null) {
            setText("N/A");
            setIcon(null);
        } else {
            setText(generator.getName());
            setIcon(null);
        }

        return this;
    }
}
