package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;

import java.util.ArrayList;
import java.util.List;

public class MainwindowTabSheet extends TabSheet {
    private List<CmdUiTab> tabsInSheet;


    public MainwindowTabSheet() {
        super();
        setSizeFull();
        addThemeVariants(TabSheetVariant.LUMO_TABS_MINIMAL);

        tabsInSheet = new ArrayList<>();
    }



    public void addTabSelectedChangeListener(TabSelectedIndexChanged change){
        this.addSelectedChangeListener(event ->{
            Tab current = event.getSelectedTab();
            int index = this.getIndexOf(current);
            change.selectedIndexChanged(index);
        });
    }

    public void addTab(CmdUiTab tab) {
        Tab imp = this.add(tab.getWindowTitle(), tab);
        this.setSelectedTab(imp);
        tabsInSheet.add(tab);
    }

    public void focusTab(CmdUiTab tab) {
        int index = tabsInSheet.indexOf(tab);
        this.setSelectedIndex(index);
    }

    public void closeTab(CmdUiTab tab) {
        int index = tabsInSheet.indexOf(tab);
        Tab impl = this.getTabAt(index);
        this.remove(impl);
        tabsInSheet.remove(tab);
    }

    public boolean hasOpenTabs() {
        return tabsInSheet.size() > 0;
    }

    public interface TabSelectedIndexChanged {

        public void selectedIndexChanged(int i);

    }
}
