package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.WrappedSession;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.modellwerkstatt.objectflow.runtime.MoWareFormattersFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

public class Turku {
    public static final String INTERNAL_VERSION = "Turkuforms (moware11) 0.1";
    private static final String HARDLOG_DIR = "/Users/danielstieger/";
    private static final String HARDLOG_FILENAME = HARDLOG_DIR + "turkulog.log";
    private static final DateTimeFormatter formatter = MoWareFormattersFactory.forDateTimePattern("hh:mm:ss.SSS", "de");

    public static boolean DEBUG_HARDLOG = true;
    static {
        DEBUG_HARDLOG = DEBUG_HARDLOG && new File(HARDLOG_DIR).canWrite();
    }

    public static String sessionToString(WrappedSession session) {
        StringBuilder sb = new StringBuilder();

        sb.append("AttributeNames:\n");
        for (String key : session.getAttributeNames()) {
            String value = ((String) session.getAttribute(key).toString());
            sb.append("     " + key + ": " + value);
        }
        return sb.toString();
    }

    public static String requestToString(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        sb.append("HeaderNames: \n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            sb.append("     " + key + ": " + value + "\n");
        }

        sb.append("\nAttributeNames:\n");
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = (String) attributeNames.nextElement();
            String value = ((String) request.getAttribute(key).toString());
            sb.append("     " + key + ": " + value);
        }

        sb.append("\nParameters\n");
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String key : paramMap.keySet()) {
            sb.append("     " + key + ": ");
            for (String value : paramMap.get(key)) {
                sb.append("'" + value + "' ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String requestToString(VaadinRequest request) {
        StringBuilder sb = new StringBuilder();

        sb.append("HeaderNames: \n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            sb.append("     " + key + ": " + value + "\n");
        }

        sb.append("\nAttributeNames:\n");
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = (String) attributeNames.nextElement();
            String value = ((String) request.getAttribute(key).toString());
            sb.append("     " + key + ": " + value);
        }

        sb.append("\nParameters\n");
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String key : paramMap.keySet()) {
            sb.append("     " + key + ": ");
            for (String value : paramMap.get(key)) {
                sb.append("'" + value + "' ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public static void logWithServlet(String source, String msg, Throwable t) {
        VaadinServlet.getCurrent().log(source + ": " + msg, t);
    }

    public static void l(String text) {
        if (DEBUG_HARDLOG) {
            try {
                File logFile = new File(HARDLOG_FILENAME);
                FileOutputStream out;

                out = new FileOutputStream(HARDLOG_FILENAME, logFile.exists());

                PrintWriter writer = new PrintWriter(out);
                writer.println("" + formatter.print(new DateTime()) + ": " + text);
                writer.close();
                out.close();

            } catch (IOException e) {
                throw new RuntimeException(e);

            }
        }
    }


    public static void clearAndDelete() {

        if (DEBUG_HARDLOG) {
            File logFile = new File(HARDLOG_FILENAME);
            if (logFile.exists()) {
                logFile.delete();
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(INTERNAL_VERSION);
    }
}

