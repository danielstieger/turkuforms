package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FakeTabSheet extends VerticalLayout implements ITurkuMainTab {
    private CmdUiTab current;


    public FakeTabSheet() {
        this.setSizeFull();
    }

    @Override
    public Component getAsComponent() {
        return this;
    }

    @Override
    public String getTabTitle() {
        return current.getWindowTitle();
    }

    @Override
    public void addTabSelectedChangeListener(TabSelectedIndexChanged change) {
        // selection change not possible
    }

    @Override
    public void addTab(CmdUiTab tab) {
        this.removeAll();
        current = tab;
        this.add(tab);
    }

    @Override
    public void focusTab(CmdUiTab tab) {
        current = tab;
        this.add(current);
    }

    @Override
    public void closeTab(CmdUiTab tab) {
        current = null;
        this.removeAll();
    }

    @Override
    public boolean hasOpenTabs() {
        return current != null;
    }

    @Override
    public void setModal(boolean modal) {
        // nothing to do here
    }
}
