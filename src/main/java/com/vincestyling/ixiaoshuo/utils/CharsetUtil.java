package com.vincestyling.ixiaoshuo.utils;

public class CharsetUtil {
    // in java, there's no unsigned byte, we need to do "& 0xFF" operation to get an unsigned value of the original byte
    public static int getByteCountOfLastIncompleteChar(byte[] bytes, int length, Encoding encoding) {
        if (encoding == Encoding.GBK || encoding == Encoding.BIG5) {
            int startIndex = 0;
            if (length > CHECK_BYTES) startIndex = length - CHECK_BYTES;
            for (int i = length - 1; i >= startIndex; --i) {
                if ((bytes[i] & 0xFF) <= 0x7F) {
                    return length - i - 1;
                }
            }
        } else if (encoding == Encoding.UTF8) {
            // if true, it is an ASCII char
            if ((bytes[length - 1] & 0xFF) <= 0x7F) return 0;
            for (int i = length - 1; i >= 0; --i) {
                // 2 = 0000 0010, if it is not the leading byte of a UTF-8 char
                if ((bytes[i] & 0xFF) >> 6 != 2) {
                    // this UTF-8 char will at least occupy 2 bytes
                    int byteCount = 2;
                    // the first two bytes have already been counted, shift them away here
                    int tempChar = (bytes[i] & 0xFF) << 2;
                    // count bit by bit
                    while ((tempChar & 0x80) >= 0x80) {
                        ++byteCount;
                        tempChar <<= 1;
                    }
                    if (length - i != byteCount) return length - i;
                    return 0;
                }
            }
        }
        return 0;
    }

    final static int CHECK_BYTES = 1024;

    public static int getByteCountOfFirstIncompleteChar(byte[] bytes, int length, Encoding encoding) {
        if (encoding == Encoding.GBK || encoding == Encoding.BIG5) {
            if (length > CHECK_BYTES) length = CHECK_BYTES;
            for (int i = 0; i < length; ++i) {
                // for the GBK or BIG5 charset, each character occupies 1 or 2 characters
                // if true, it is an ASCII char or might be the 2nd byte of a GBK or BIG5 character
                if ((bytes[i] & 0xFF) <= 0x7F) { // test the second byte
                    return i + 1;
                }
            }
        } else if (encoding == Encoding.UTF8) {
            // if true, it is an ASCII char
            if ((bytes[0] & 0xFF) <= 0x7F) return 0;

            int i = 0;
            while ((bytes[i] & 0xFF) >> 6 == 2) {
                ++i;
            }
            return i;
        }
        return 0;
    }
}
