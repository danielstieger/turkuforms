package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.WrappedSession;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.modellwerkstatt.objectflow.runtime.MoWareFormattersFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

public class Turku {
    public static final String INTERNAL_VERSION = "turkuforms (moware17) autumn 25";
    private static final DateTimeFormatter formatter = MoWareFormattersFactory.forDateTimePattern("hh:mm:ss.SSS", "de");

    public static String cookiesToString(VaadinRequest request) {
        String info = "";

        if (request.getCookies() != null){
            for (Cookie c: request.getCookies()) {
                info += c.getName()+ ": " + c.getValue() + " " + c.getMaxAge() + "  ";
            }

        } else {
            info = "null";
        }

        return info;
    }

    public static void logWithServlet(String source, String msg, Throwable t) {
        if (t != null) {
            VaadinServlet.getCurrent().log(source + ": " + msg, t);
        } else {
            VaadinServlet.getCurrent().log(source + ": " + msg);
        }
    }

    public static void main(String[] args) {
        System.out.println(INTERNAL_VERSION);
    }
}

