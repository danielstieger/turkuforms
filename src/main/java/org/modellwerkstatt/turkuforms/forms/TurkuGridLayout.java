package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_FormContainer;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.FormHeading;
import org.modellwerkstatt.turkuforms.util.LeftRight;
import org.modellwerkstatt.turkuforms.util.OverflowMenu;

import java.util.List;

public class TurkuGridLayout<DTO> extends VerticalLayout implements IToolkit_FormContainer<DTO> {
    private ITurkuFactory factory;
    private LeftRight topContainer;
    private OverflowMenu menu;
    private FormHeading heading;

    private boolean needsFullWith = false;
    private boolean multipleColumns;
    private List<String> colConstraints;
    private List<String> rowConstraints;

    private int childsAdded = 0;
    private FlexComponent containerToAddComponent;


    public TurkuGridLayout(ITurkuFactory factory) {
        super();
        this.factory = factory;
        containerToAddComponent = this;
    }

    @Override
    public void setLayoutConstraints(List<String> colConstraints, List<String> rowConstraints) {
        this.colConstraints = colConstraints;
        this.rowConstraints = rowConstraints;

        multipleColumns = colConstraints.size() > 1;
        this.setSizeUndefined();
        if (hasStarWeight(colConstraints)) {
            needsFullWith = true;
            this.setWidthFull();
        }
        if (hasStarWeight(rowConstraints)) { this.setHeightFull(); }
    }

    @Override
    public void addChildren(IToolkit_Form iToolkit_form) {
        int currentRow = childsAdded / colConstraints.size();
        int currentCol = childsAdded - (currentRow * colConstraints.size());

        if (currentCol >= colConstraints.size() || currentRow >= rowConstraints.size()) {
            throw new RuntimeException("This can not happen! Col/Row constraints can t be correct; col: " + currentCol + " / " +
                    colConstraints.size() + ", row: " + currentRow + " / " + rowConstraints.size()); }

        int currentColConstraint = getWeight(colConstraints.get(currentCol));
        int currentRowConstraint = getWeight(rowConstraints.get(currentRow));

        // Start with a new HorizontalLayout ?
        if (currentCol == 0 && multipleColumns) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setSizeUndefined();

            if (needsFullWith) hl.setWidthFull();
            if (currentRowConstraint > 0) { hl.setHeightFull(); }

            this.add(hl);
            containerToAddComponent = hl;
            if (currentRowConstraint == -1) {
                this.setFlexGrow(0, hl);
            } else {
                this.setFlexGrow(currentRowConstraint, hl);
            }

        }

        int childConstraint = multipleColumns ? currentColConstraint : currentRowConstraint;
        // TODO: should we also apply sizeFull/sizeUndefined()
        // add child now
        Component childCmpt = (Component) iToolkit_form;
        containerToAddComponent.add(childCmpt);
        if (childConstraint == -1) {
            containerToAddComponent.setFlexGrow(0, childCmpt);
        } else {
            containerToAddComponent.setFlexGrow(childConstraint, childCmpt);
        }

        childsAdded ++;
    }

    @Override
    public void addMenuAndSetButtons(MenuSub menuSub) {
        if (topContainer == null) { installTopContainer(); }
        menu = new OverflowMenu();
        topContainer.add(menu);
        menu.initialize(factory, menuSub, null);
    }

    @Override
    public void setTitleText(String s) {
        if (topContainer == null) { installTopContainer(); }
        heading.setHeading(s);
    }

    @Override
    public void setProblems(List<IOFXProblem> list) {
        if (topContainer == null) { installTopContainer(); }
        heading.flag(list);
    }

    @Override
    public Object myRequestFocus() {
        return null;
    }

    @Override
    public void afterFullUiInitialized() {

    }

    @Override
    public boolean selectionChanged(IOFXSelection<DTO> iofxSelection) {
        return false;
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection<DTO> iofxSelection) {

    }

    @Override
    public void gcClear() {
        factory = null;
    }

    private boolean hasStarWeight(List<String> weights) {
        for (String st: weights) {
            if (getWeight(st) > 0) { return true; }
        }
        return false;
    }

    private int getWeight(String st) {
        if (st.equals("-1")) { return -1; }
        else if (st.equals("1*")) { return 1; }
        else if (st.equals("2*")) { return 2; }
        else if (st.equals("3*")) { return 3; }
        else if (st.equals("4*")) { return 4; }
        else if (st.equals("5*")) { return 5; }
        else { throw new RuntimeException("This can not happen: got a weight of '" + st + "'!"); }
    }

    private void installTopContainer() {
        topContainer = new LeftRight();
        heading = new FormHeading();
        topContainer.add(heading);
        topContainer.spacer();
        this.addComponentAtIndex(0, topContainer);
    }
}
