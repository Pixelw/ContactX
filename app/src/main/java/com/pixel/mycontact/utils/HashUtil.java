package com.pixel.mycontact.utils;

import java.util.zip.CRC32;

/**
 * @author Carl Su
 * @date 2020/5/8
 */
public class HashUtil {
    public static String toCrc32(byte[] bytes){
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return String.valueOf(crc32.getValue());
    }
}
