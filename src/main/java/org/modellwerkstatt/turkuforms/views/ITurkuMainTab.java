package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;

public interface ITurkuMainTab {

    Component getComponent();

    String getTabTitle();

    void addTabSelectedChangeListener(TabSelectedIndexChanged change);

    void addTab(CmdUiTab tab);
    void focusTab(CmdUiTab tab);

    void closeTab(CmdUiTab tab);

    boolean hasOpenTabs();

    interface TabSelectedIndexChanged {
        void selectedIndexChanged(int i);
    }
}
