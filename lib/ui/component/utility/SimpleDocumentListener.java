package lib.ui.component.utility;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Simplified DocumentListener interface that provides a single update() method
 * instead of requiring implementation of three separate methods.
 * 
 * This is a convenience class for cases where you want the same action
 * to occur on insert, remove, or changed events.
 */
@FunctionalInterface
public interface SimpleDocumentListener extends DocumentListener {
    
    /**
     * Called when the document is updated in any way
     * @param e DocumentEvent
     */
    void update(DocumentEvent e);
    
    @Override
    default void insertUpdate(DocumentEvent e) {
        update(e);
    }
    
    @Override
    default void removeUpdate(DocumentEvent e) {
        update(e);
    }
    
    @Override
    default void changedUpdate(DocumentEvent e) {
        update(e);
    }
}
