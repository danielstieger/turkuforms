package org.modellwerkstatt.addons.components.support;


import com.vaadin.flow.component.html.Div;
import org.modellwerkstatt.dataux.runtime.core.IDelegateChange;
import org.modellwerkstatt.dataux.runtime.core.IPagePaneSelCrtl;
import org.modellwerkstatt.dataux.runtime.core.ISelectionController;
import org.modellwerkstatt.dataux.runtime.extensions.ICustomDataUxElement;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.dataux.runtime.utils.MoJSON;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class CustomDataUxListBound<T> implements ICustomDataUxElement<T> {
    protected IToolkit_Form<T> formImpl;
    protected HashMap<String, String> labelToPathMap;

    public CustomDataUxListBound() {
        labelToPathMap = new HashMap<>(10);
    }

    public void setToolkitFormImpl(IToolkit_Form<T> impl) {
        formImpl = impl;
    }

    @Override
    public void loadList(List<T> list, IOFXSelection iofxSelection) {
        formImpl.loadList(list, iofxSelection);
    }

    @Override
    public IToolkit_Form getToolkitImplementation() {
        return formImpl;
    }

    @Override
    public void addDelegateInfo(String delegateName, String path, String label) {
        labelToPathMap.put(label, path);
    }

    public String getString(T obj, String label) {
        if (!labelToPathMap.containsKey(label)) {
            throw new RuntimeException("The requested label " + label + "was not suplied with overwrite labels in the custom ux-element");
        }

        String path = labelToPathMap.get(label);
        return MoJSON.<String>get(obj, path);
    }
    public BigDecimal getBigDecimal(T obj, String label) {
        if (!labelToPathMap.containsKey(label)) {
            throw new RuntimeException("The requested label " + label + "was not supplied with overwrite labels in the custom ux-element");
        }

        String path = labelToPathMap.get(label);
        Object value = MoJSON.get(obj, path);
        if (value == null) {
            return null;

        } else if (value instanceof  BigDecimal) {
            return  (BigDecimal) value;

        } else if (value instanceof String) {
            return new BigDecimal((String) value);

        } else {
            throw new RuntimeException("Unclear how to handle " + value + " of type " + value.getClass().getSimpleName() + " for label " + label + ", boundObjectType " + obj.getClass().getSimpleName());
        }

    }
    public boolean hasLabel(String label) {
         return labelToPathMap.containsKey(label);
    }


    @Override
    public boolean selectionChanged(IOFXSelection iofxSelection, boolean b) {
        return false;
    }

    @Override
    public String saveAndValidate() {
        return null;
    }

    @Override
    public List<IDelegateChange> collectDelegateChanges() {
        return null;
    }

    @Override
    public void forceNotEditable() {
    }

    @Override
    public void initializeGen(IToolkit_UiFactory iToolkit_uiFactory, IPagePaneSelCrtl iPagePaneSelCrtl, ISelectionController.Binding binding, Menu menu) {
    }

    @Override
    public void preDelayedAfterFullUiInitialized() {
    }

    @Override
    public void gcClear() {
    }

    @Override
    public IOFXSelection getSelection(Class aClass, boolean b) {
        return null;
    }

    @Override
    public void pushSelection(IOFXSelection iofxSelection) {
    }
}
