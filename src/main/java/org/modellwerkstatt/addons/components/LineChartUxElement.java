package org.modellwerkstatt.addons.components;

import org.modellwerkstatt.addons.components.support.CustomDataUxListBound;
import org.modellwerkstatt.addons.components.support.MultiSeriesUxListBound;

public class LineChartUxElement<T> extends MultiSeriesUxListBound<T> {


    public LineChartUxElement() {
        super();

        setToolkitFormImpl(new LineChartToolkitImpl<T>(this));
    }

}
