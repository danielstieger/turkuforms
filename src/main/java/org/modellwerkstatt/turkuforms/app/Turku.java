package org.modellwerkstatt.turkuforms.app;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.modellwerkstatt.objectflow.runtime.MoWareFormattersFactory;

import java.io.*;

public class Turku {
    public static final String VERSION = "Turkuforms (moware11) 0.1";
    public static final boolean DEBUG_HARDLOG = true;
    private static final String HARDLOG_FILENAME = "/Users/danielstieger/turkulog.txt";
    private static final DateTimeFormatter formatter = MoWareFormattersFactory.forDateTimePattern("hh:mm:ss.SSS", "de");



    public static void l(String text) {
        if (!(DEBUG_HARDLOG)) {
            return;
        }

        try {
            File logFile = new File(HARDLOG_FILENAME);
            FileOutputStream out;

            out = new FileOutputStream(HARDLOG_FILENAME, logFile.exists());

            PrintWriter writer = new PrintWriter(out);
            writer.println("" + formatter.print(new DateTime()) + ": " + text);
            writer.close();
            out.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void clearAndDelete() {
        if (!(DEBUG_HARDLOG)) {
            return;
        }

        File logFile = new File(HARDLOG_FILENAME);
        if (logFile.exists()) {
            logFile.delete();
        }
    }


    public static void main(String[] args) {
        System.out.println(VERSION);
    }
}

