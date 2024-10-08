package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.dom.Element;
import org.modellwerkstatt.turkuforms.core.TurkuApp;

import java.util.ArrayList;
import java.util.List;

public class TabSheetMDI extends TabSheet implements ITurkuMainTab {
    private List<CmdUiTab> tabsInSheet;

    @Override
    public Component getAsComponent() {
        return this;
    }

    public TabSheetMDI() {
        super();
        setSizeFull();
        addThemeVariants(TabSheetVariant.LUMO_TABS_MINIMAL);
        addClassName("MainwindowTabSheet");
        tabsInSheet = new ArrayList<>();
        getElement().getChild(0).setAttribute("class", "MainwindowTabSheetTabs");
    }

    @Override
    public String getTitleForNavbar() {
        // not to adjust in app
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

        if (! hasOpenTabs()) {
            adjustTopBarColorOrNull(null);
        }
    }

    @Override
    public boolean hasOpenTabs() {
        return tabsInSheet.size() > 0;
    }

    @Override
    public void setModal(boolean modal) {
        if (modal) { addClassName("ModalTab"); }
        else { removeClassName("ModalTab"); }

        int selected = getSelectedIndex();
        for (int i = 0 ; i < tabsInSheet.size(); i++) {
            if (i != selected) {
                getTabAt(i).setEnabled(! modal);
            }
        }
    }

    @Override
    public void adjustTopBarColorOrNull(String col) {
        ((TurkuApp) this.getParent().get()).adjustTopBarColorOrNull(col);
    }

    @Override
    public void adjustTitle() {
        int selected = getSelectedIndex();
        getSelectedTab().setLabel(tabsInSheet.get(selected).getWindowTitle());
    }

    @Override
    public void adjustTabStyle(CmdUiTab ui, String col) {
        int index = tabsInSheet.indexOf(ui);
        Element tab = getTabAt(index).getElement();

        tab.executeJs("turku.setTurkuCommandColor($0, $1)", tab, col);
    }
}
