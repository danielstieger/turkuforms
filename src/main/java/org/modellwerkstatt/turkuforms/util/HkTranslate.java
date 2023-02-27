package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.Key;

import java.util.HashMap;

public class HkTranslate {
    final static private HashMap<String, Key> toVaadin = new HashMap<>();
    static {
        toVaadin.put("A", Key.KEY_A);
        toVaadin.put("B", Key.KEY_B);
        toVaadin.put("C", Key.KEY_C);
        toVaadin.put("D", Key.KEY_D);
        toVaadin.put("E", Key.KEY_E);
        toVaadin.put("F", Key.KEY_F);
        toVaadin.put("G", Key.KEY_G);
        toVaadin.put("H", Key.KEY_H);
        toVaadin.put("I", Key.KEY_I);
        toVaadin.put("J", Key.KEY_J);
        toVaadin.put("K", Key.KEY_K);
        toVaadin.put("L", Key.KEY_L);
        toVaadin.put("M", Key.KEY_M);
        toVaadin.put("N", Key.KEY_N);
        toVaadin.put("O", Key.KEY_O);
        toVaadin.put("P", Key.KEY_P);
        toVaadin.put("Q", Key.KEY_Q);
        toVaadin.put("R", Key.KEY_R);
        toVaadin.put("S", Key.KEY_S);
        toVaadin.put("T", Key.KEY_T);
        toVaadin.put("U", Key.KEY_U);
        toVaadin.put("V", Key.KEY_V);
        toVaadin.put("W", Key.KEY_W);
        toVaadin.put("X", Key.KEY_X);
        toVaadin.put("Y", Key.KEY_Y);
        toVaadin.put("Z", Key.KEY_Z);
        toVaadin.put("N0", Key.NUMPAD_0);
        toVaadin.put("N1", Key.NUMPAD_1);
        toVaadin.put("N2", Key.NUMPAD_2);
        toVaadin.put("N3", Key.NUMPAD_3);
        toVaadin.put("N4", Key.NUMPAD_4);
        toVaadin.put("N5", Key.NUMPAD_5);
        toVaadin.put("N6", Key.NUMPAD_6);
        toVaadin.put("N7", Key.NUMPAD_7);
        toVaadin.put("N8", Key.NUMPAD_8);
        toVaadin.put("N9", Key.NUMPAD_9);
        // toVaadin.put("TAB", Key.);
        // toVaadin.put("SPACE", Key.);
        // toVaadin.put("ENTER", Key.);
        toVaadin.put("ESCAPE", Key.ESCAPE);
        toVaadin.put("ESC", Key.ESCAPE);
        toVaadin.put("BACKSPACE", Key.BACKSPACE);
        toVaadin.put("DELETE", Key.DELETE);
        toVaadin.put("UP", Key.ARROW_UP);
        toVaadin.put("DOWN", Key.ARROW_DOWN);
        toVaadin.put("LEFT", Key.ARROW_LEFT);
        toVaadin.put("RIGHT", Key.ARROW_RIGHT);
        toVaadin.put("PAGE-UP", Key.PAGE_UP);
        toVaadin.put("PAGE-DOWN", Key.PAGE_DOWN);
        toVaadin.put("HOME", Key.HOME);
        toVaadin.put("END", Key.END);
        toVaadin.put("KEYPAD-0", Key.NUMPAD_0);
        toVaadin.put("KEYPAD-1", Key.NUMPAD_1);
        toVaadin.put("KEYPAD-2", Key.NUMPAD_2);
        toVaadin.put("KEYPAD-3", Key.NUMPAD_3);
        toVaadin.put("KEYPAD-4", Key.NUMPAD_4);
        toVaadin.put("KEYPAD-5", Key.NUMPAD_5);
        toVaadin.put("KEYPAD-6", Key.NUMPAD_6);
        toVaadin.put("KEYPAD-7", Key.NUMPAD_7);
        toVaadin.put("KEYPAD-8", Key.NUMPAD_8);
        toVaadin.put("KEYPAD-9", Key.NUMPAD_9);
        // toVaadin.put("KEYPAD-UP", Key.);
        // toVaadin.put("KEYPAD-DOWN", Key.);
        // toVaadin.put("KEYPAD-LEFT", Key.);
        // toVaadin.put("KEYPAD-RIGHT", Key.);
        toVaadin.put("PLUS", Key.ADD);
        toVaadin.put("MINUS", Key.MINUS);
        toVaadin.put("EQUALS", Key.EQUAL);
        toVaadin.put("ADD", Key.ADD);
        toVaadin.put("SUBTRACT", Key.SUBTRACT);
        toVaadin.put("MULTIPLY", Key.MULTIPLY);
        toVaadin.put("DIVIDE", Key.DIVIDE);
        toVaadin.put("F1", Key.F1);
        toVaadin.put("F2", Key.F2);
        toVaadin.put("F3", Key.F3);
        toVaadin.put("F4", Key.F4);
        toVaadin.put("F5", Key.F5);
        toVaadin.put("F6", Key.F6);
        toVaadin.put("F7", Key.F7);
        toVaadin.put("F8", Key.F8);
        toVaadin.put("F9", Key.F9);
        toVaadin.put("F10", Key.F10);
        toVaadin.put("F11", Key.F11);
        toVaadin.put("F12", Key.F12);
        toVaadin.put("INSERT", Key.INSERT);
        // toVaadin.put("UPD", Key.);
        // toVaadin.put("GO", Key.);
        // toVaadin.put("UNDEFINED", Key.);
    }


    public static Key trans(String s) {
        if (!toVaadin.containsKey(s)) {
            throw new IllegalArgumentException("Turkuforms.HkTranslate is currently not aware of hotkey '" + s + "',");
        }
        return toVaadin.get(s);
    }
}
