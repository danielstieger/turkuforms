package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import org.modellwerkstatt.dataux.runtime.core.FocusController;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_FormContainer;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.util.ArrayList;
import java.util.List;

public class SliderLayout<DTO> extends VerticalLayout implements IToolkit_FormContainer<DTO> {
    private ITurkuAppFactory factory;
    private LeftRight topContainer;
    private TurkuMenu menu;
    private FormHeading heading;
    private FocusController<IToolkit_Form> focusController;

    private List<Integer> colConstraints;
    private List<Integer> rowConstraints;

    private int childsAdded;


    public SliderLayout(ITurkuAppFactory factory) {
        super();
        Peculiar.shrinkSpace(this);

        this.factory = factory;
        focusController = new FocusController<>();
        childsAdded = 0;

        addClassName("TurkuSliderGrid");
    }

    @Override
    public void skipFocus(int i) {
        focusController.skipFocus(i);
    }

    @Override
    public void setLayoutConstraints(List<String> colConstraints, List<String> rowConstraints) {
        this.colConstraints = new ArrayList<>();
        for (String c: colConstraints) { this.colConstraints.add(TurkuGridLayout.getWeight(c)); };
        this.rowConstraints = new ArrayList<>();
        for (String c: rowConstraints) { this.rowConstraints.add(TurkuGridLayout.getWeight(c)); };

        if (this.colConstraints.size() != 1) {
            throw new RuntimeException("This can not happen. Turku SliderLayout() does only allow for one column layouts.");
        }

        if (TurkuGridLayout.hasStarWeight(this.rowConstraints)) {
            this.setHeightFull();
        }

        if (TurkuGridLayout.hasStarWeight(this.colConstraints)) {
            this.setWidthFull();
        }
    }


    @Override
    public void addChildren(IToolkit_Form child) {
        focusController.addChild(child);


        if (childsAdded + 1 == rowConstraints.size()) {
            // we are done.
            List<IToolkit_Form> allChilds = focusController.getChildren();


            Component last = (Component) allChilds.get(allChilds.size() - 1);
            for (int i = allChilds.size() - 2; i >= 0; i--) {
                Component first = (Component) allChilds.get(i);

                SplitLayout current = new SplitLayout(first, last, SplitLayout.Orientation.VERTICAL);
                current.setSizeFull();
                current.addThemeVariants(SplitLayoutVariant.LUMO_MINIMAL);

                int sumWeight = sumWeight(rowConstraints, i);
                int pos = 100 * rowConstraints.get(i) / sumWeight;
                current.setSplitterPosition(pos);
                last = current;
            }

            add(last);


        } else if (childsAdded + 1 > rowConstraints.size()) {
            throw new RuntimeException("This can not happen. Can not add " + (childsAdded+1) + " with " + rowConstraints.size());
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
    public void rootForm() {
        if (topContainer == null) { installTopContainer(); }
        heading.inRootPos();
    }

    static public int sumWeight(List<Integer> lst, int start) {
        int sum = 0;
        for (int i = start; i < lst.size(); i++) {
            if (lst.get(i) <= 0) {
                throw new RuntimeException("Can not handle " + lst.get(i) + " weight in Turku SliderLayout.");
            }
            sum += lst.get(i);
        }
        return sum;
    }

    @Override
    public Object myRequestFocus() {
        return focusController.myRequestFocus();
    }

    @Override
    public void afterFullUiInitialized() {
        focusController.afterFullUiInitialized();
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

    private void installTopContainer() {
        topContainer = new LeftRight("TurkuHeadingTopPane");
        heading = new FormHeading();
        topContainer.add(heading);
        topContainer.spacer();
        this.addComponentAtIndex(0, topContainer);
    }
}
