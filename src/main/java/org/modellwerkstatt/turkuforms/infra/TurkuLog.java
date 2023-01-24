package org.modellwerkstatt.turkuforms.infra;

import org.joda.time.format.DateTimeFormatter;
import org.modellwerkstatt.objectflow.runtime.MoWareFormattersFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.joda.time.DateTime;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TurkuLog {
    private static final boolean HARDLOG_AVAILABLE = true;
    private static final String HARDLOG_FILENAME = "/Users/danielstieger/turkulog.txt";
    private static final DateTimeFormatter formatter = MoWareFormattersFactory.forDateTimePattern("hh:mm:ss.SSS", "de");



    public static void l(String text) {
        if (!(HARDLOG_AVAILABLE)) {
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
        if (!(HARDLOG_AVAILABLE)) {
            return;
        }

        File logFile = new File(HARDLOG_FILENAME);
        if (logFile.exists()) {
            logFile.delete();
        }
    }

}

