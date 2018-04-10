package com.zemiak.ggz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;

public class Crc {
    public static String compute(String fileName) throws FileNotFoundException, IOException {
        File f = new File(fileName);
        FileInputStream fis = new FileInputStream(f);
        CRC32 crcMaker = new CRC32();
        byte[] buffer = new byte[65536];
        int bytesRead;
        while((bytesRead = fis.read(buffer)) != -1) {
            crcMaker.update(buffer, 0, bytesRead);
        }

        return String.valueOf(crcMaker.getValue());
    }
}
