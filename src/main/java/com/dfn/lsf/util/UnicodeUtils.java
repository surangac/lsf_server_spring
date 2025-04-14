package com.dfn.lsf.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class UnicodeUtils {
    public static String getNativeStringFromCompressed(String sUnicode) {
        int i = 0;
        int buffindex = 0;
        char[] buf = new char[sUnicode.length()];
        int iLen = 0;
        char ch;
        char next;
        String prev = "";

        iLen = sUnicode.length();

        while (i < iLen) {
            ch = getNext(sUnicode, i++);
            if (ch == '\\') {
                if ((next = getNext(sUnicode, i++)) == 'u') {
                    if ((iLen - i) >= 4) {
                        buf[buffindex++] = processUnicode(sUnicode.substring(i, i + 4));
                        prev = sUnicode.substring(i, i + 2);

                        i += 4;
                    } else {
                        buf[buffindex++] = '\\';
                        buf[buffindex++] = 'u';
                        while (i < iLen) buf[buffindex++] = sUnicode.charAt(i++);
                        i = iLen;
                    }
                } else if (next == -1) {
                    return (new String(buf, 0, buffindex));
                } else {
                    buf[buffindex++] = '\\';
                    i--;
                    //buf[buffindex++] = next;
                }
            } else {
                if ((iLen - (i - 1)) >= 2) {
                    String nex = prev + sUnicode.substring(i - 1, i + 1);
                    buf[buffindex++] = processUnicode(nex);
                    i += 1;
                } else if (getNext(sUnicode, i++) == -1) {
                    return (new String(buf, 0, buffindex));
                }
            }
        }
        return (new String(buf, 0, buffindex));
    }

    public static String getNativeString(String sUnicode) {
        int i = 0;
        int buffindex = 0;
        char[] buf = new char[sUnicode.length()];
        int iLen = 0;
        char ch;
        char next;

        iLen = sUnicode.length();

        while (i < iLen) {
            ch = getNext(sUnicode, i++);
            if (ch == '\\') {
                if ((next = getNext(sUnicode, i++)) == 'u') {
                    if ((iLen - i) >= 4) {
                        buf[buffindex++] = processUnicode(sUnicode.substring(i, i + 4));
                        i += 4;
                    } else {
                        buf[buffindex++] = '\\';
                        buf[buffindex++] = 'u';
                        while (i < iLen) buf[buffindex++] = sUnicode.charAt(i++);
                        i = iLen;
                    }
                } else if (next == -1) {
                    return (new String(buf, 0, buffindex));
                } else {
                    buf[buffindex++] = '\\';
                    i--;
                    //buf[buffindex++] = next;
                }
            } else {
                buf[buffindex++] = ch;
            }
        }
        return (new String(buf, 0, buffindex));
    }

    public static String getAsciiString(String sUnicode) {
        int i = 0;
        int buffindex = 0;
        StringBuffer buf = new StringBuffer();
        int iLen = 0;
        char ch;
        char next;

        iLen = sUnicode.length();

        while (i < iLen) {
            ch = getNext(sUnicode, i++);
            if (ch == '\\') {
                if ((next = getNext(sUnicode, i++)) == 'u') {
                    if ((iLen - i) >= 4) {
                        //buf[buffindex++] = processAscii(sUnicode.substring(i,i+4));
                        buf.append(processAscii(sUnicode.substring(i, i + 4)));
                        i += 4;
                    } else {
                        //buf[buffindex++] = '\\';
                        //buf[buffindex++] = 'u';
                        buf.append("\\u");
                        while (i < iLen) buf.append(sUnicode.charAt(i++));
                        i = iLen;
                    }
                } else if (next == -1) {
                    return buf.toString();
                } else {
                    buf.append('\\');
                    i--;
                    //buf[buffindex++] = next;
                }
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /*
    * Returns the next char from the string
    */
    private static char getNext(String sUnicode, int i) {
        if (i < sUnicode.length())
            return sUnicode.charAt(i);
        else
            return (char) -1;
    }

    /*
    * Converts the 4 digit code to the native char
    */
    private static char processUnicode(String sUnicode) {
        char ch;
        int d = 0;
        loop:
        for (int i = 0; i < 4; i++) {
            ch = sUnicode.charAt(i);
            switch (ch) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    d = (d << 4) + ch - '0';
                    break;
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    d = (d << 4) + 10 + ch - 'a';
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                    d = (d << 4) + 10 + ch - 'A';
                    break;
                default:
                    break loop;
            }
        }
        return (char) d;
    }

    private static String processAscii(String sUnicode) {
        char ch;
        int d = 0;
        loop:
        for (int i = 0; i < 4; i++) {
            ch = sUnicode.charAt(i);
            switch (ch) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    d = (d << 4) + ch - '0';
                    break;
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    d = (d << 4) + 10 + ch - 'a';
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                    d = (d << 4) + 10 + ch - 'A';
                    break;
                default:
                    break loop;
            }
        }
        return "&#" + (int) d + ";";
    }

    public static String getCompressedUnicodeString(String sNative) {
        try {
            String prev = "";
            StringBuffer sUnicode = new StringBuffer("");
            char[] acNative = sNative.toCharArray();

            for (int i = 0; i < acNative.length; i++) {
                String str = charToHex(acNative[i]);
                String next = str.substring(0, 2);
                if (next.equals(prev)) {
                    sUnicode.append(str.substring(2));
                } else {
                    sUnicode.append("\\u");
                    sUnicode.append(str);
                    prev = next;
                }
            }
            return sUnicode.toString();
        } catch (Exception ex)
        {
            return "";// return Settings.NULL_STRING;
        }
    }

    public static String charToHex(char c) {
        byte hi = (byte) (c >>> 8);
        byte lo = (byte) (c & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    }

    static public String byteToHex(byte b) {
        char hexDigit[] =
                {
                        '0', '1', '2', '3', '4', '5', '6', '7',
                        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
                };
        char[] array = {hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f]};
        return new String(array);
    }



    public static String getArabicNumbers(String str) {

        char[] text = null;
        text = str.toCharArray();
        str = null;

        char base = '\u0660' - '\u0030';
        char minDigit = '\u0030';
        for (int i = 0, e = text.length; i < e; ++i) {
            char c = text[i];
            if (c >= minDigit && c <= '\u0039') {
                text[i] = (char) (c + base);
            }
        }
        return new String(text);
    }

    public static String asciiToNative(String string) {
//        try {
//            return new String(string.getBytes(), Language.getCharSet());
//        } catch (Exception e) {
        return string;
//        }
    }

    public static String UTFtoClear(String s)
    {
        String clearArabic = "";
        try
        {
            clearArabic  = new String();
            byte[] b = s.getBytes("Cp1256");
            String ret = new String(b, "UTF-8");
            clearArabic = ret;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return clearArabic;
    }

    public static String clearToUTF(String s)
    {
        String UTFArabic = "";
        try
        {   UTFArabic= new String();
            byte[] b = s.getBytes("UTF-8");
            String ret = new String(b, "Cp1256");
            UTFArabic = ret;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return UTFArabic;
    }

    public static String ISO8859_1ToUTF(String s)
    {
        String UTFArabic = "";
        try
        {   UTFArabic= new String();

            byte[] bv = s.getBytes("ISO-8859-1");
            String ret1 = new String(bv,"Cp1256");
            byte[] b = ret1.getBytes("UTF-8");
            String ret = new String(b, "Cp1256");
            UTFArabic = ret;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return UTFArabic;
    }


    public static String UTFToISO8859_1(String s)
    {
        String UTFArabic = "";
        try
        {   UTFArabic= new String();

            byte[] bv = s.getBytes("Cp1256");
            String ret1 = new String(bv, "UTF-8");
            byte[] b = ret1.getBytes("Cp1256");
            String ret = new String(b, "ISO-8859-1");

            UTFArabic = ret;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return UTFArabic;
    }



    public static String clearToISO8859_1(String s)
    {
        String UTFArabic = "";
        try
        {   UTFArabic= new String();
            byte[] b = s.getBytes("ISO-8859-6");
            String ret = new String(b, "ISO-8859-1");
            UTFArabic = ret;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return UTFArabic;
    }

    public static String ISO8859_1toclear(String input ) {
        if(input == null){
            return "";
        }
        String ret = "";
        try
        {
            byte[] bytes;
            bytes = input.getBytes("ISO-8859-1");
            ret = new String(bytes, "ISO-8859-6");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return ret;
    }

    private static String decodeResponseNew(String str , String chaset) {
        try {
            Charset charsetInput = Charset.forName(chaset);
            CharsetDecoder decoder = charsetInput.newDecoder();
            CharBuffer cbuf;
            cbuf = decoder.decode(ByteBuffer.wrap(str.getBytes()));
            return String.valueOf(cbuf);
        } catch (Exception e) {
        }
        return null;
    }

    public static String getUnicodeStringTemp(String sNative) {
        try {

            StringBuffer sUnicode = new StringBuffer("");
            char[] acNative =  new String(decodeResponseNew(sNative, "windows-1252").getBytes(), "UTF-8").toCharArray();
            for (int i = 0; i < acNative.length; i++) {
                sUnicode.append("\\u");
                sUnicode.append(charToHex(acNative[i]));
            }
            return sUnicode.toString();
        } catch (Exception ex) {
        }
        return  null;
    }

    public static String getUnicodeString(String sNative) {
        if(sNative == null){
            return "";
        }
        try {
            StringBuilder sUnicode = new StringBuilder("");
            char[] acNative = sNative.toCharArray();
            for (int i = 0; i < acNative.length; i++) {
                sUnicode.append("\\u");
                sUnicode.append(charToHex(acNative[i]));
            }
            return sUnicode.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
