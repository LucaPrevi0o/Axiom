package lib.ui.component;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Simplified document listener interface
 */
abstract class SimpleDocumentListener implements DocumentListener {
    
    public abstract void onChange();
    
    @Override
    public void insertUpdate(DocumentEvent e) { onChange(); }
    
    @Override
    public void removeUpdate(DocumentEvent e) { onChange(); }
    
    @Override
    public void changedUpdate(DocumentEvent e) { onChange(); }
}
