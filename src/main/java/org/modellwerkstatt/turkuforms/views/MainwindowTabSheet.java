package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;

import java.util.ArrayList;
import java.util.List;

public class MainwindowTabSheet extends Composite<Component> {
    private TabSheet commandTabSheet;
    private List<CmdUiTab> tabsInSheet;


    public MainwindowTabSheet() {
        tabsInSheet = new ArrayList<>();
        // TODO: dont we need a TabChange listener?
    }

    @Override
    protected Component initContent() {
        commandTabSheet = new TabSheet();
        commandTabSheet.setSizeFull();
        commandTabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_MINIMAL);
        return commandTabSheet;
    }

    public void addTab(CmdUiTab tab) {
        Tab imp = commandTabSheet.add(tab.getWindowTitle(), tab);
        commandTabSheet.setSelectedTab(imp);
        tabsInSheet.add(tab);
    }

    public void focusTab(CmdUiTab tab) {
        int index = tabsInSheet.indexOf(tab);
        commandTabSheet.setSelectedIndex(index);
    }

    public void closeTab(CmdUiTab tab) {
        int index = tabsInSheet.indexOf(tab);
        Tab impl = commandTabSheet.getTabAt(index);
        commandTabSheet.remove(impl);
        tabsInSheet.remove(tab);
    }

    public boolean hasOpenTabs() {
        return tabsInSheet.size() > 0;
    }
}
