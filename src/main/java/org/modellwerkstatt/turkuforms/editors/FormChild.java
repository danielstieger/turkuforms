package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.shared.Tooltip;
import org.modellwerkstatt.dataux.runtime.extensions.IDataUxDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;
import org.modellwerkstatt.turkuforms.forms.TurkuDelegatesForm;
import org.modellwerkstatt.turkuforms.util.Workarounds;

abstract public class FormChild<T extends Component> implements IToolkit_TextEditor {
    protected Label label;
    protected T inputField;
    protected Component rightPart;

    protected IDataUxDelegate<?> delegate;
    protected TurkuDelegatesForm<?> turkuDelegatesForm;
    protected boolean wideOption;

    public FormChild(T theField) {
        inputField = theField;
        rightPart = inputField;
        label = new Label();
        wideOption = false;
    }

    public void enableKeyReleaseEvents() {
        // for textfield only, in case hooks are used (calc tax of value etc.)
        throw new IllegalStateException("Not implemented. Probably resort to js?");
    }

    public void setDelegate(IDataUxDelegate iDataUxDelegate) { delegate = iDataUxDelegate; }

    public void attachedToForm(TurkuDelegatesForm<?> dlgtFrm) { turkuDelegatesForm = dlgtFrm; }

    public void setLabelTooltip(String s) {
        Workarounds.addMlToolTipIfNec(s, label);
    }

    public void setLabel(String s) {
        label.setText(s);
    }

    public void turkuFocus() { /* not focussable per se */ }

    public Object getEditor() { return inputField; }

    public Object getLabel() { return label; }

    public Object getRightPartComponent() { return rightPart; }

    @Override
    public void setOption(Option... options) {
        if (IToolkit_TextEditor.has(Option.WIDE, options)) {
            wideOption = true;
        }
    }

    public boolean isWideOption(){
        return wideOption;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " for " + (delegate == null ? "null" : delegate.getPropertyName());
    }

    public void gcClear() {
        delegate = null;
        turkuDelegatesForm = null;

    }

}
