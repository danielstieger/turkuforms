package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.core.FocusController;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_FormContainer;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.util.ArrayList;
import java.util.List;

public class TurkuGridLayout<DTO> extends VerticalLayout implements IToolkit_FormContainer<DTO> {
    private ITurkuAppFactory factory;
    private LeftRight topContainer;
    private TurkuMenu menu;
    private FormHeading heading;
    private FocusController<IToolkit_Form> focusController;

    private boolean needsFullWith = false;
    private boolean multipleColumns;
    private List<Integer> colConstraints;
    private List<Integer> rowConstraints;

    private int childsAdded = 0;
    private FlexComponent containerToAddComponent;
    private int hLevel;


    public TurkuGridLayout(ITurkuAppFactory factory) {
        super();
        this.factory = factory;
        containerToAddComponent = this;
        Peculiar.shrinkSpace(this);
        focusController = new FocusController<>();

    }

    @Override
    public void setHLevel(int numComponent, int level) {
        Turku.l("TurkuGrid.setHLevel( " + numComponent + ", " + level);

        hLevel = level;
        for (IToolkit_Form form: focusController.getChildren()) {
            form.setHLevel(++numComponent, level);
        }
        if (heading != null) { heading.setHLevel(numComponent, level); }
    }

    @Override
    public void setLayoutConstraints(List<String> colConstraints, List<String> rowConstraints) {
        this.colConstraints = new ArrayList<>();
        for (String c: colConstraints) { this.colConstraints.add(getWeight(c)); };
        this.rowConstraints = new ArrayList<>();
        for (String c: rowConstraints) { this.rowConstraints.add(getWeight(c)); };

        multipleColumns = this.colConstraints.size() > 1;
        this.setSizeUndefined();
        if (hasStarWeight(this.colConstraints)) {
            needsFullWith = true;
            this.setWidthFull();
        }
        if (hasStarWeight(this.rowConstraints)) { this.setHeightFull(); }
    }

    @Override
    public void addChildren(IToolkit_Form child) {
        focusController.addChild(child);

        int currentRow = childsAdded / colConstraints.size();
        int currentCol = childsAdded - (currentRow * colConstraints.size());

        if (currentCol >= colConstraints.size() || currentRow >= rowConstraints.size()) {
            throw new RuntimeException("This can not happen! Col/Row constraints can t be correct; col: " + currentCol + " / " +
                    colConstraints.size() + ", row: " + currentRow + " / " + rowConstraints.size()); }

        int currentColConstraint = colConstraints.get(currentCol);
        int currentRowConstraint = rowConstraints.get(currentRow);

        // Start with a new HorizontalLayout ?
        if (currentCol == 0 && multipleColumns) {
            HorizontalLayout hl = new HorizontalLayout();
            Peculiar.shrinkSpace(hl);
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
        Component childCmpt = (Component) child;
        containerToAddComponent.add(childCmpt);
        if (childConstraint == -1) {
            containerToAddComponent.setFlexGrow(0, childCmpt);
        } else {
            containerToAddComponent.setFlexGrow(childConstraint, childCmpt);
        }

        childsAdded ++;
    }

    @Override
    public void addMenuAndSetButtons(Menu menuSub) {
        if (topContainer == null) { installTopContainer(); }
        menu = new TurkuMenu();
        topContainer.add(menu);
        menu.initialize(factory, menuSub);
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
        return focusController.myRequestFocus();
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

    public static boolean hasStarWeight(List<Integer> weights) {
        for (Integer st: weights) {
            if (st > 0) { return true; }
        }

        return false;
    }

    public static int getWeight(String st) {
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
