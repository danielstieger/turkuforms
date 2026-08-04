package org.modellwerkstatt.addons.components.support;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.modellwerkstatt.dataux.runtime.core.IDelegateChange;
import org.modellwerkstatt.dataux.runtime.core.IPagePaneSelCrtl;
import org.modellwerkstatt.dataux.runtime.core.ISelectionController;
import org.modellwerkstatt.dataux.runtime.customcomponents.ExtCmpt;
import org.modellwerkstatt.dataux.runtime.extensions.ICustomDataUxElement;
import org.modellwerkstatt.dataux.runtime.genspecification.MenuAction;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.dataux.runtime.utils.MoJSON;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MultiSeriesUxListBound<T> implements ICustomDataUxElement<T> {
    protected IToolkit_Form<T> formImpl;
    protected String xValuePath;
    protected boolean xValuePathFirstSet;
    protected String xValueFormat;
    protected List<Series> allSeries;
    protected String title;
    protected String yTitle;

    public MultiSeriesUxListBound() {
        allSeries = new ArrayList<Series>();
        xValuePathFirstSet = false;
    }

    public List<Series> getSeries() {
        return allSeries;
    }
    public String getTitle() { return title; }
    public String getyTitle() { return yTitle; }

    public String getXValueAsString(T root) {
        Object obj = MoJSON.get(root, xValuePath);

        if (obj == null) {
            return "";

        } else if (xValueFormat != null && obj instanceof DateTime) {
            return  DateTimeFormat.forPattern(xValueFormat).print((DateTime) obj);

        } else if (xValueFormat != null && obj instanceof LocalDate) {
            return  DateTimeFormat.forPattern(xValueFormat).print((LocalDate) obj);

        } else if (xValueFormat != null && (obj instanceof BigDecimal)) {
            return new DecimalFormat(xValueFormat).format((BigDecimal) obj);

        } else {
            return "" + obj;
        }

    }

    public BigDecimal getYValueAsBigDecimal(T obj, String seriesName) {
        Series lookingFor = allSeries.stream().filter(s -> s.getName().equals(seriesName)).findFirst().orElse(null);
        if (lookingFor == null) throw new RuntimeException("Series " + seriesName + " not found");

        return MoJSON.get(obj, lookingFor.getPath());
    }

    @Override
    public void addDelegateInfo(String delegateName, String path, String label, String format) {
        if (!xValuePathFirstSet) {
            xValuePath = path;
            xValueFormat = format;
            xValuePathFirstSet = true;

        } else {
            allSeries.add(new Series(delegateName, path, label, format));

        }
    }

    @Override
    public void setOption(String key, String val) {
        if (ExtCmpt.LINECHART_TITLE.equals(key)) title = val;
        else if (ExtCmpt.LINECHART_YTITLE.equals(key)) yTitle = val;
        else throw new RuntimeException("Invalid option: " + key);
    }

    @Override
    public boolean selectionChanged(IOFXSelection iofxSelection, boolean b) {
        return true;
    }

    @Override
    public void loadList(List<T> list, IOFXSelection iofxSelection) {
        formImpl.loadList(list, iofxSelection);
    }

    @Override
    public String saveAndValidate() {
        return null;
    }

    @Override
    public List<IDelegateChange> collectDelegateChanges() {
        return null;
    }

    @Override
    public void forceNotEditable() {

    }

    @Override
    public void initializeGen(IToolkit_UiFactory iToolkitUiFactory, IPagePaneSelCrtl iPagePaneSelCrtl, ISelectionController.Binding binding, MenuAction menuAction) {

    }

    @Override
    public void preDelayedAfterFullUiInitialized() {
        if(xValuePath == null) {
            throw new RuntimeException("XPOS not specified (somewhere as delegate label)");
        }
    }

    @Override
    public IOFXSelection getSelection(Class aClass, boolean b) {
        return null;
    }

    @Override
    public void pushSelection(IOFXSelection iofxSelection) {

    }

    public void setToolkitFormImpl(IToolkit_Form<T> impl) {
        formImpl = impl;
    }
    @Override
    public IToolkit_Form<T> getToolkitImplementation() {
        return formImpl;
    }


    @Override
    public void gcClear() {
        formImpl = null;
    }

    public static class Series {

        private final String delegateName;
        private final String path;
        public String name;
        private final String format;


        public Series(String delegateName, String path, String name, String format) {
            this.delegateName = delegateName;
            this.path = path;
            this.name = name;
            this.format = format;
        }

        public String getName() {
            return name;
        }
        public String getDelegateName() {
            return delegateName;
        }
        public String getPath() {
            return path;
        }
        public String getFormat() {
            return format;
        }
    }

}
