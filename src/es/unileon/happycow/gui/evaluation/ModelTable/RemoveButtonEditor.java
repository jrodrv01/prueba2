/*
 * 
 */
package es.unileon.happycow.gui.evaluation.ModelTable;

import es.unileon.happycow.controller.evaluation.IEvaluationController;
import es.unileon.happycow.model.composite.Valoration;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author dorian
 */
public class RemoveButtonEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener, MouseListener {

    private JButton buttonRemove;
    private ModelTable model;
    private JTable table;
    private boolean isButtonColumnEditor;
    private IEvaluationController controller;

    public RemoveButtonEditor(ModelTable model, JTable table, IEvaluationController controller) {
        buttonRemove = new JButton(new javax.swing.ImageIcon(
                getClass().getResource("/images/unchecked.png")));
        buttonRemove.setToolTipText("Eliminar");
        buttonRemove.setBorderPainted(false);
        buttonRemove.setContentAreaFilled(false);
        buttonRemove.setFocusPainted(false);
        buttonRemove.addActionListener(this);

        this.table = table;
        this.model = model;
        this.controller = controller;
    }

    @Override
    public Object getCellEditorValue() {
        return buttonRemove;
    }

    @Override
    public boolean isCellEditable(EventObject eo) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject eo) {
        return true;
    }

    /*
     *	The button has been pressed. Stop editing and invoke the custom Action
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int row = table.convertRowIndexToModel(table.getEditingRow());
        Valoration val = model.getValoration(row);
        fireEditingStopped();

        if (isButtonColumnEditor) {
            controller.removeValoration(val.getId());
        }

    }

    /*
     *  When the mouse is pressed the editor is invoked. If you then then drag
     *  the mouse to another cell before releasing it, the editor is still
     *  active. Make sure editing is stopped when the mouse is released.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (table.isEditing()
                && table.getCellEditor() == this) {
            isButtonColumnEditor = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isButtonColumnEditor
                && table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        isButtonColumnEditor = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {

        return buttonRemove;
    }
}
