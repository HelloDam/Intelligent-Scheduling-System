package com.dam.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TxtUtil {
    public static String read( File f, String charset) throws Exception
    {
        FileInputStream fstream = new FileInputStream(f);
        try{
            int fileSize = (int)f.length();
            if(fileSize > 1024*512){
                throw new Exception("File too large to read! size=" + fileSize);
            }

            byte[] buffer = new byte[ fileSize ];
            fstream.read(buffer);
            return new String(buffer, charset);
        }finally
        {
            try{ fstream.close();}catch (Exception e){}
        }
    }

    public static void write(File f, String text, String charset) throws Exception
    {
        FileOutputStream fstream = new FileOutputStream(f);
        try{
            fstream.write( text.getBytes( charset ));
        }finally
        {
            fstream.close();
        }
    }
}
