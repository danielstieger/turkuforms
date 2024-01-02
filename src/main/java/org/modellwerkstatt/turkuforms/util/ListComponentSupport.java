package org.modellwerkstatt.turkuforms.util;

import org.modellwerkstatt.dataux.runtime.core.IDelegateChange;
import org.modellwerkstatt.dataux.runtime.core.IPagePaneSelCrtl;
import org.modellwerkstatt.dataux.runtime.core.ISelectionController;
import org.modellwerkstatt.dataux.runtime.extensions.ICustomDataUxElement;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.util.List;

public abstract class ListComponentSupport<T> implements ICustomDataUxElement<T> {

    @Override
    public void addDelegateInfo(String s, String s1, String s2) {

    }

    @Override
    public boolean selectionChanged(IOFXSelection iofxSelection, boolean b) {
        return false;
    }

    @Override
    public void loadList(List<T> list, IOFXSelection iofxSelection) {

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
