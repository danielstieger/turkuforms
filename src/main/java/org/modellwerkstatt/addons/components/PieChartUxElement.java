package org.modellwerkstatt.addons.components;

import org.modellwerkstatt.addons.components.support.CustomDataUxListBound;

public class PieChartUxElement<T> extends CustomDataUxListBound<T> {


    public PieChartUxElement() {
        super();

        setToolkitFormImpl(new PieChartToolkitImpl<T>(this));
    }

}
