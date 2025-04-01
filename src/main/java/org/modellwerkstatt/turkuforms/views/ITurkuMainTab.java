package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;

public interface ITurkuMainTab extends ITurkuMainAdjust {

    Component getAsComponent();

    String getTitleForNavbar();

    void addTabSelectedChangeListener(TabSelectedIndexChanged change);

    void addTab(CmdUiTab tab);
    void focusTab(CmdUiTab tab);

    void closeTab(CmdUiTab tab);

    boolean hasOpenTabs();

    void setModal(boolean modal);



    interface TabSelectedIndexChanged {
        void selectedIndexChanged(int i);
    }
}
