package org.modellwerkstatt.turkuforms.forms;

import org.modellwerkstatt.dataux.runtime.extensions.ITableCellStringConverter;


public class TurkuTableCol {
    public int position;
    public ITableCellStringConverter mowareConverter;
    public String propertyName;
    public String headerName;
    public int widthInPercent;


    public TurkuTableCol(int pos, String propname, String header, ITableCellStringConverter conv, int width) {
        position = pos;
        mowareConverter = conv;
        propertyName = propname;
        headerName = header;
        widthInPercent = width;

    }

}
