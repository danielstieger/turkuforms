package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.dom.Style;
import org.modellwerkstatt.objectflow.runtime.OFXConsoleHelper;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.util.ArrayList;
import java.util.List;

public class MainwindowTabSheet extends TabSheet implements ITurkuMainTab {
    private List<CmdUiTab> tabsInSheet;
    private Style currentSelectedStyle;

    @Override
    public Component getAsComponent() {
        return this;
    }

    public MainwindowTabSheet() {
        super();
        setHeightFull();
        addThemeVariants(TabSheetVariant.LUMO_TABS_MINIMAL);
        addClassName("MainwindowTabSheet");
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
                addStyle(current, tabsInSheet.get(index));
                change.selectedIndexChanged(index);
            }
        });
    }

    @Override
    public void addTab(CmdUiTab tab) {
        tabsInSheet.add(tab);

        Tab imp = this.add(tab.getWindowTitle(), tab);
        this.setSelectedTab(imp);
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
        tabsInSheet.remove(tab);
        this.remove(impl);
    }

    @Override
    public boolean hasOpenTabs() {
        return tabsInSheet.size() > 0;
    }

    @Override
    public void setModal(boolean modal) {
        int selected = getSelectedIndex();
        for (int i = 0 ; i < tabsInSheet.size(); i++) {
            if (i == selected) {
                if (modal) { getTabAt(i).addClassName("ModalTab"); }
                else { getTabAt(i).removeClassName("ModalTab"); }

            } else {
                getTabAt(i).setEnabled(! modal);
            }
        }
    }

    public void addStyle(Tab tab, CmdUiTab cmdUi){
        if (currentSelectedStyle != null) {
            currentSelectedStyle.remove("color");
            currentSelectedStyle.remove("border-top");
        }

        String col = cmdUi.getColorOrNull();
        if (col == null) { col = "var(--lumo-primary-color)"; }
        currentSelectedStyle = tab.getElement().getStyle().set("color", col).set("border-top", "2px solid " + col);
    }
}
