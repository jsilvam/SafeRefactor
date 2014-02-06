/**
 * @(#)EditCanvasPanel.java
 *
 * Copyright (c) 1996-2009 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and
 * contributors of the JHotDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * the copyright holders. For details see accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.awt.Color;
import javax.swing.*;
import javax.swing.text.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.gui.event.DrawingAttributeEditorHandler;
import org.jhotdraw.gui.JAttributeSlider;
import org.jhotdraw.text.JavaNumberFormatter;
import org.jhotdraw.util.*;
import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * The EditCanvasPanel can be used to edit the attributes of a Drawing.
 *
 * @see org.jhotdraw.draw.Drawing
 *
 * @author Werner Randelshofer
 * @version $Id: EditCanvasPanel.java 593 2009-12-21 13:37:58Z rawcoder $
 */
@SuppressWarnings("unchecked")
public class EditCanvasPanel extends javax.swing.JPanel {

    private ResourceBundleUtil labels;
    private Drawing drawing;
    private JAttributeSlider opacitySlider;
    private JColorChooser colorChooser;
    private DrawingAttributeEditorHandler<Double> opacityFieldHandler, opacitySliderHandler;

    /** Creates new form. */
    public EditCanvasPanel() {
        labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        initComponents();
        colorButton.putClientProperty("Quaqua.Button.style", "colorWell");
        opacitySlider = new JAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
        opacityPopupButton.add(opacitySlider);
        opacityPopupButton.putClientProperty("JButton.buttonType", "toolbar");
        add(opacityPopupButton);
        NumberFormatter nf = new NumberFormatter();
        nf.setMaximum(1d);
        nf.setMinimum(0d);
        opacityField.setFormatterFactory(JavaNumberFormatter.createFormatterFactory(0d, 1d, 100d));
        opacityFieldHandler=new DrawingAttributeEditorHandler<Double>(CANVAS_FILL_OPACITY, opacityField, null);
        opacitySliderHandler=new DrawingAttributeEditorHandler<Double>(CANVAS_FILL_OPACITY, opacitySlider, null);
    }

    private JColorChooser getColorChooser() {
            if (colorChooser == null) {
                colorChooser = new JColorChooser();
            }
            return colorChooser;
    }
    /**
     * Sets the GridConstrainer to be edited by this panel.
     * 
     * @param newValue The GridConstrainer.
     */
    public void setDrawing(Drawing newValue) {
        drawing = newValue;
        // XXX - This does not work, we must pass the drawing editor here!
        opacitySliderHandler.setDrawing(drawing);
        opacityFieldHandler.setDrawing(drawing);
        updatePanel();
    }

    /**
     * Updates the drawing due to changes made on this panel.
     */
    private void updateDrawing() {
        if (drawing != null) {
            drawing.willChange();
            drawing.fireUndoableEditHappened(
                    CANVAS_FILL_COLOR.setUndoable(drawing, colorButton.getBackground())
                    );
            drawing.changed();
        }
    }

    /**
     * Updates the panel due to changes made on the drawing.
     */
    private void updatePanel() {
        if (drawing != null) {
            colorButton.setBackground(drawing.get(CANVAS_FILL_COLOR));
        }
    }

    /**
     * Returns the GridConstrainer currently being edited by this panel.
     * 
     * @return The GridConstrainer.
     */
    public Drawing getDrawing() {
        return drawing;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JFrame f = new JFrame("Drawing Settings2");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new EditCanvasPanel());
        f.pack();
        f.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        bgColorButtonGroup = new javax.swing.ButtonGroup();
        colorLabel = new javax.swing.JLabel();
        colorButton = new javax.swing.JButton();
        opacityLabel = new javax.swing.JLabel();
        opacityField = new org.jhotdraw.gui.JAttributeTextField<Double>();
        opacityPopupButton = new org.jhotdraw.gui.JPopupButton();

        setLayout(new java.awt.GridBagLayout());

        colorLabel.setText(labels.getString("attribute.canvasFillColor.text")); // NOI18N
        colorLabel.setToolTipText(labels.getString("attribute.backgroundColor.toolTipText")); // NOI18N
        add(colorLabel, new java.awt.GridBagConstraints());

        colorButton.setText(" ");
        colorButton.setToolTipText(labels.getString("attribute.backgroundColor.toolTipText")); // NOI18N
        colorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorButtonPerformed(evt);
            }
        });
        add(colorButton, new java.awt.GridBagConstraints());

        opacityLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jhotdraw/draw/action/images/attributeOpacity.png"))); // NOI18N
        opacityLabel.setToolTipText(labels.getString("attribute.opacity.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(opacityLabel, gridBagConstraints);

        opacityField.setColumns(3);
        add(opacityField, new java.awt.GridBagConstraints());

        opacityPopupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jhotdraw/draw/action/images/popupIcon.png"))); // NOI18N
        opacityPopupButton.setToolTipText(labels.getString("attribute.opacity.toolTipText")); // NOI18N
        add(opacityPopupButton, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents
    private void colorButtonPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorButtonPerformed
     if (drawing != null) {
        Color color = getColorChooser().showDialog(this, labels.getString("attribute.backgroundColor"), 
               drawing.get(CANVAS_FILL_COLOR)
               );
        colorButton.setBackground(color);
        updateDrawing();
        }
}//GEN-LAST:event_colorButtonPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgColorButtonGroup;
    private javax.swing.JButton colorButton;
    private javax.swing.JLabel colorLabel;
    private org.jhotdraw.gui.JAttributeTextField opacityField;
    private javax.swing.JLabel opacityLabel;
    private org.jhotdraw.gui.JPopupButton opacityPopupButton;
    // End of variables declaration//GEN-END:variables
}
