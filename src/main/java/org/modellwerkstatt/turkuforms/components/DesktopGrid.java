package org.modellwerkstatt.turkuforms.components;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.notification.Notification;

@JsModule("./src/desktopgrid.ts")
@Tag("desktop-grid")
public class DesktopGrid extends Component {


    public DesktopGrid() {

        this.getElement().addAttachListener(event -> {
            init();
        });
    }

    protected void init() {
        String total = "";

        for (int i = 0; i < 100; i++) {
            total += genRowData(i, new String[]{"Dan"+ i, "Man", "000" + i, "CTO"}) + ",";
        }

        total = total.substring(0, total.length()-1);


        // this.getElement().executeJs("this.items = $0", total);
        // this.getElement().executeJs("alert($0)", total);
        this.getElement().callJsFunction("initData", total, this);
    }



    @ClientCallable
    protected void select(int index) {
        Notification.show(" Selected index " + index, 2000, Notification.Position.BOTTOM_END);
    }


    protected String genRowData(int index, String... data) {
        StringBuilder b =new StringBuilder();
        b.append("{");

        for (int i = 0; i < data.length; i++) {
            b.append("\"index\":"+ index + ",\"c" + (i+1) +"\":\"" + data[i]+ "\",");
        }
        b.deleteCharAt(b.length()-1);
        b.append("}");
        return b.toString();
    }
}
