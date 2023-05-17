package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.util.ArrayList;
import java.util.List;

public class MainwindowTabSheet extends TabSheet implements ITurkuMainTab {
    private List<CmdUiTab> tabsInSheet;

    @Override
    public Component getAsComponent() {
        return this;
    }

    public MainwindowTabSheet() {
        super();
        setSizeFull();
        addThemeVariants(TabSheetVariant.LUMO_TABS_MINIMAL);

        tabsInSheet = new ArrayList<>();
    }

    @Override
    public String getTabTitle() {
        return "";
    }

    @Override
    public void addTabSelectedChangeListener(TabSelectedIndexChanged change){
        this.addSelectedChangeListener(event ->{
            Tab current = event.getSelectedTab();
            if (current != null) {
                int index = this.getIndexOf(current);
                change.selectedIndexChanged(index);
            }
        });
    }

    @Override
    public void addTab(CmdUiTab tab) {
        Tab imp = this.add(tab.getWindowTitle(), tab);
        this.setSelectedTab(imp);
        tabsInSheet.add(tab);
    }

    @Override
    public void focusTab(CmdUiTab tab) {
        int index = tabsInSheet.indexOf(tab);
        this.setSelectedIndex(index);
    }

    @Override
    public void closeTab(CmdUiTab tab) {
        int index = tabsInSheet.indexOf(tab);
        Tab impl = this.getTabAt(index);
        this.remove(impl);
        tabsInSheet.remove(tab);
    }

    @Override
    public boolean hasOpenTabs() {
        return tabsInSheet.size() > 0;
    }
}
