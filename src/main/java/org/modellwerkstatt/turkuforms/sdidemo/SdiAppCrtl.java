package org.modellwerkstatt.turkuforms.sdidemo;


import java.util.ArrayList;
import java.util.List;

public class SdiAppCrtl {
    private static final int MAIN = 0;
    public boolean authenticated = false;
    public String userName = "";

    public IToolkit_MainWindow mainPage;
    public List<EventCommandContainer> otherPages = new ArrayList<>();


    public SdiAppCrtl(String usr) {
        userName = usr;
    }
    public boolean isAuthenticated() { return authenticated; }

    public void disable(EventCommandContainer own) {
            otherPages.stream().forEach(it -> { if (it != own) { it.toggleDisable(); }});
    }

    public int numCommands() {
        return otherPages.size();
    }

    public void adjustData(String data) {
        otherPages.stream().forEach(ecc -> ecc.showMessage("Data adjusted to " + data));
    }

    public void startCmdByParams(IToolkit_Window page, DynParams params) {
        if (!isAuthenticated()) {
            throw new RuntimeException("This can not happen - not authenticated!");

        } else if (! params.hasCmdName()) {
            page.setMessage("URL ERROR - No command to start specified!");

        } else {
            Cmd newCommand = new Cmd(this, params.get(0), params.get(1), params.get(2));
            EventCommandContainer container = new EventCommandContainer(this, page, newCommand);
            otherPages.add(container);

            container.exec();
        }


    }

}
