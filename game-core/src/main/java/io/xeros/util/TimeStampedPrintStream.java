package io.xeros.util;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeStampedPrintStream extends PrintStream {

    public TimeStampedPrintStream(@NotNull OutputStream out) {
        super(out);
    }

    @Override
    public void println(String string) {
        LocalDateTime date = LocalDateTime.now();
        super.println("[" + date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "] " + string);
    }

    @Override
    public void print(String string) {
        LocalDateTime date = LocalDateTime.now();
        super.print("[" + date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "] " + string);
    }


}
