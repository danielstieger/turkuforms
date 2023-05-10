package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;

public class FakeTabSheet implements ITurkuMainTab {
    CmdUiTab primary;

    @Override
    public Component getComponent() {
        return primary;
    }

    @Override
    public String getTabTitle() {
        return primary.getWindowTitle();
    }

    @Override
    public void addTabSelectedChangeListener(TabSelectedIndexChanged change) {
        // selection change not possible
    }

    @Override
    public void addTab(CmdUiTab tab) {
        primary = tab;
    }

    @Override
    public void focusTab(CmdUiTab tab) {
        primary = tab;
    }

    @Override
    public void closeTab(CmdUiTab tab) {
        primary = null;
    }

    @Override
    public boolean hasOpenTabs() {
        return primary != null;
    }
}
