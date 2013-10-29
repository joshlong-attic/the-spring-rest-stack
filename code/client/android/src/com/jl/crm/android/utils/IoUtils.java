package com.jl.crm.android.utils;

import android.os.Environment;

import org.springframework.util.Assert;

import java.io.*;

/**
 * Commonly used routines.
 */
public abstract class IoUtils {

    public static File writableFile(String fileName) {
        final File extStorageDir = Environment.getExternalStorageDirectory();
        File tmpFile = new File(new File(extStorageDir, "spring-crm"), fileName),
                parent = tmpFile.getParentFile();

        String tmpFilePath = tmpFile.getAbsolutePath();
        Assert.isTrue(Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState()), "the external SD card must be writable to write to '" + tmpFilePath + "'.");
        Assert.isTrue(parent.exists() || parent.mkdirs(), "the parent directory required to write to the SD card ('" + tmpFilePath + "') does not exist and could not be created.");
        return tmpFile;
    }

    public static byte[] readFully(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    public static void copyStreams(InputStream is, OutputStream os) throws IOException {
        Assert.notNull(is);
        Assert.notNull(os);
        byte[] b = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(b)) != -1) {
            os.write(b, 0, bytesRead);
        }
        try {
            is.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            os.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}