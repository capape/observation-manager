package de.lehmannet.om.ui.navigation.observation.utils;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TeeLog extends PrintStream {

    private PrintStream console = null;
    private final byte[] prefix;

    public static final Object syncMe = new Object();

    public TeeLog(final PrintStream file) {

        this(file, "");

    }

    public TeeLog(final PrintStream file, final String prefix) {

        // Parent class writes to file
        super(file, false, StandardCharsets.UTF_8);

        // Prefix we set for every entry
        this.prefix = prefix.getBytes(StandardCharsets.UTF_8);

        // We write to console
        this.console = System.out;

    }

    @Override
    public void write(final byte[] buf, final int off, final int len) {

        if ((buf == null) || (buf.length == 0)) {
            return;
        }

        final String now = "  " + new Date().toString() + "\t";
        try {
            synchronized (TeeLog.syncMe) {
                if (!((buf[0] == (byte) 13) // (byte 13 -> carage return) So if
                                            // cr is send we do not put date &
                                            // prefix in
                                            // advance
                        || (buf[0] == (byte) 10)) // (byte 10 -> line feed) So if lf is
                                                  // send we do not put date & prefix
                                                  // in advance
                ) {
                    this.write(this.prefix, 0, this.prefix.length);
                    this.write(now.getBytes(StandardCharsets.UTF_8), 0, now.length());
                }
                this.write(buf, off, len);

                if (!((buf[0] == (byte) 13) // (byte 13 -> carage return) So if
                                            // cr is send we do not put date &
                                            // prefix in
                                            // advance
                        || (buf[0] == (byte) 10)) // (byte 10 -> line feed) So if lf is
                                                  // send we do not put date & prefix
                                                  // in advance
                ) {
                    this.console.write(this.prefix, 0, this.prefix.length);
                    this.console.write(now.getBytes(StandardCharsets.UTF_8), 0, now.length());
                }
                this.console.write(buf, off, len);
            }
        } catch (final Exception e) {
            // Can't do anything in here
        }

    }

    @Override
    public void flush() {

        super.flush();
        synchronized (TeeLog.syncMe) {
            this.console.flush();
        }

    }

}