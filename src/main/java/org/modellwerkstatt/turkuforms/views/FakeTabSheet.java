package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FakeTabSheet extends VerticalLayout implements ITurkuMainTab {
    private CmdUiTab current;
    private DrawerToggle drawerToggle;


    public FakeTabSheet(DrawerToggle dt) {
        this.setSizeFull();
        drawerToggle = dt;
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
        drawerToggle.setEnabled(false);
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
        drawerToggle.setEnabled(true);
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
