package com.pixel.mycontact.utils;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Carl Su
 * @date 2019/12/17
 */
public class StringUtils {
    /**
     * 使用gzip压缩字符串
     *
     * @param str 要压缩的字符串
     * @return 压缩后的字符串字节
     */
    public static byte[] gzipString(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gzip != null) {
            try {
                gzip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] byteArray = out.toByteArray();
        LogUtil.d("StringGzip", "String size before gzip:"
                + str.getBytes().length +
                ", String gzipped:" + byteArray.length);
        LogUtil.d("unzip", unGzipToString(byteArray));
        return byteArray;
    }

    public static String unGzipToString(byte[] bytes) {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPInputStream gzip = null;
        String string = null;
        try {
            gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int offset;
            while ((offset = gzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            string = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gzip != null) {
            try {
                gzip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

}
