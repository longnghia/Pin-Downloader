package com.ln.pindownloader.Utils;

import com.ln.pindownloader.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

public class IOUtils {
    public static void closeQuietly(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(OutputStream outputStream) {
        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Reader reader) {
        try {
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
