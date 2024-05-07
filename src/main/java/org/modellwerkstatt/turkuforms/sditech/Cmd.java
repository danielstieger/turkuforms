package org.modellwerkstatt.turkuforms.sditech;

public class Cmd {
    public String cmdName;
    public String param1;
    public String param2;

    public Cmd() {

    }

    public Cmd(SdiAppCrtl crtl, String cmdName, String param1, String param2) {
        this.cmdName = cmdName;
        this.param1 = param1;
        this.param2 = param2;
    }



    @Override
    public String toString() {
        return "Command " + cmdName + " with params (" + param1 + ", " + param2 + ")";
    }
}
