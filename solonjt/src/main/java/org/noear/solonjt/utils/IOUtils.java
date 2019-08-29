package org.noear.solonjt.utils;

import java.io.*;

public class IOUtils {
    public static final InputStream fromBytes(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static final byte[] toBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }


    public static final String toString(InputStream stream,String charset) throws IOException {
        try {
            stream.reset();
        }catch (Exception ex){}

        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toString(charset);
    }

    public static final InputStream outToIn(OutputStream out){
        ByteArrayOutputStream out2 = (ByteArrayOutputStream) out;
        final ByteArrayInputStream in2 = new ByteArrayInputStream(out2.toByteArray());
        return in2;
    }

    public static byte[] getFileBytes(File file) throws IOException {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        return toBytes( new FileInputStream(file));
    }
}
