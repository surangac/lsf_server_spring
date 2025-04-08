package com.dfn.lsf.service.security;

import java.math.BigInteger;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Created by manodyas on 7/3/2015.
 */
@Component
@RequiredArgsConstructor
public class CustomEncryption { 
    final static String key = "RbPesT9zCBs4d9nj";

    public static String getEncrypted(String id) {
        return binToHex(getXoR(getBinaryArray(id), getBinaryArray(key)));
    }

    public static String getBinaryArray(String userName) {
        StringBuilder bitArray = new StringBuilder();
        for (int i = 0; i < userName.length(); i++) {
            String binaryString = decimalToBinary((int) (userName.charAt(i)));// get ASCII value of the Character & convert it to binary
            int tempLength = binaryString.length();
            if (tempLength < 8) {
                for (int j = tempLength; j < 8; j++) {
                    binaryString = "0" + binaryString; // if the length of the bit array is less than 8 append 0's to the begin
                }
            }
            bitArray.append(binaryString);
        }
        return bitArray.toString();// always multiplication of 8
    }

    public static String getXoR(String s1, String s2) {
        StringBuilder stringBuffer = new StringBuilder();
        int max = s1.length();
        if (max < s2.length()) {
            max = s2.length();
            for (int i = s1.length(); i < max; i++) {
                s1 = "0" + s1;
            }
        } else {
            for (int i = s2.length(); i < max; i++) {
                s2 = "0" + s2;
            }
        }
        for (int j = 0; j < max; j++) {
            if ((s1.charAt(j) == '0' && s2.charAt(j) == '0') || (s1.charAt(j) == '1' && s2.charAt(j) == '1')) {
                stringBuffer.append("0");
            } else {
                stringBuffer.append("1");
            }
        }
        return stringBuffer.toString(); // size is the length of the largest bit array
    }

    public static String binToHex(String binary) {
        return (new BigInteger(binary, 2).toString(16)); // return the hexa decimal string of the result bit array
    }

    public static String getDecrypted(String encryptedID) {
        StringBuilder decrypted = new StringBuilder();
        String[] asciiArry = null;
        asciiArry = getAsciiArray(getXoR(hexToBin(encryptedID), getBinaryArray(key)));
        for (int k = 0; k < asciiArry.length; k++) {
            decrypted.append(asciiToChar(Integer.parseInt(asciiArry[k])));
        }

        return decrypted.toString();
    }

    public static String hexToBin(String s) {
        String temp = new BigInteger(s, 16).toString(2); //get binary from Hex
        if (temp.length() % 8 == 0) {
            return temp;
        } else {
            int rem = 8 - temp.length() % 8;
            for (int j = 0; j < rem; j++) {
                temp = "0" + temp; // if the length of the bit array is not 8 append 0's to the beginning
            }
            return temp;
        }
    }

    public static String decimalToBinary(int decimal) {
        String binary = "";
        while (decimal != 0) {
            binary = (decimal % 2) + binary;
            decimal /= 2;
        }
        return binary;
    }

    public static String[] getAsciiArray(String array) {
        int inLength = array.length();
        int arLength = inLength / 8;
        int left = inLength % 8;
        if (left > 0) {
            ++arLength;
        }
        String ar[] = new String[arLength];
        String tempText = array;
        for (int x = 0; x < arLength; ++x) {

            if (tempText.length() > 8) {
                ar[x] = tempText.substring(0, 8);
                tempText = tempText.substring(8);
            } else {
                ar[x] = tempText;
            }
            ar[x] = String.valueOf(binaryToDecimal(ar[x]));
        }


        return ar;
    }

    public static char asciiToChar(int ascii) {
        char character = (char) ascii;
        return character;
    }

    public static int binaryToDecimal(String c) {
        int decimalValue = Integer.parseInt(c, 2);
        return decimalValue;
    }
}
