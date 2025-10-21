package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.turkuforms.core.TurkuApp;
import org.modellwerkstatt.turkuforms.core2.TurkuMainWin2;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import static org.modellwerkstatt.turkuforms.views.TabSheetMDI.isOldTurkuApp;

public class TabSheetFake extends VerticalLayout implements ITurkuMainTab {
    private CmdUiTab current;
    private DrawerToggle drawerToggle;
    private int numTabs;

    public TabSheetFake(DrawerToggle dt) {
        Peculiar.shrinkSpace(this);
        this.setSizeFull();
        drawerToggle = dt;
    }

    @Override
    public Component getAsComponent() {
        return this;
    }

    @Override
    public String getTitleForNavbar() {
        return current.getWindowTitle();
    }

    @Override
    public void addTabSelectedChangeListener(TabSelectedIndexChanged change) {
        // selection change not possible
    }

    private void disableCurrentVis() {
        if (current != null) {
            ((Component) current).setVisible(false);
        }
    }
    @Override
    public void addTab(CmdUiTab tab) {
        disableCurrentVis();

        current = tab;
        drawerToggle.setEnabled(false);
        numTabs++;
        this.add(tab);

        if (!isOldTurkuApp(this.getParent().get())) {
            String url = tab.getAdjustedUrl();
            UI.getCurrent().getPage().getHistory().replaceState(null, url);
        }
    }

    @Override
    public void focusTab(CmdUiTab tab) {
        disableCurrentVis();

        current = tab;
        ((Component) current).setVisible(true);

        if (!isOldTurkuApp(this.getParent().get())) {
            String url = tab.getAdjustedUrl();
            UI.getCurrent().getPage().getHistory().replaceState(null, url);
        }
    }

    @Override
    public void closeTab(CmdUiTab tab) {
        current = null;
        numTabs--;
        this.remove(tab);

        if (! hasOpenTabs()) {
            drawerToggle.setEnabled(true);
            adjustTopBarColorOrNull(null);
        }

    }

    @Override
    public boolean hasOpenTabs() {
        return numTabs > 0;
    }

    @Override
    public void adjustTopBarColorOrNull(String col) {
        // also used to reset the col
        if (current != null) {
            current.getElement().executeJs("turku.setTurkuCommandColor($0, $1)", current, col);
        }

        Object mainWindow = this.getParent().get();
        if (isOldTurkuApp(mainWindow)) {
            ((TurkuApp) mainWindow).adjustTopBarColorOrNull(col);
        } else {
            ((TurkuMainWin2) mainWindow).adjustTopBarColorOrNull(col);
        }

    }

    @Override
    public void adjustTitle() {
        Object mainWindow = this.getParent().get();

        if (isOldTurkuApp(mainWindow)) {
            ((TurkuApp) mainWindow).setOptionalTabTitleInNavbar(getTitleForNavbar());
        } else {
            ((TurkuMainWin2) mainWindow).setOptionalTabTitleInNavbar(getTitleForNavbar());
        }
    }

    @Override
    public void adjustTabStyle(CmdUiTab ui, String col) {

    }

    @Override
    public void setModal(boolean modal) {
        // nothing to do here
    }
}
