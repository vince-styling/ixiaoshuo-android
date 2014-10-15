package com.vincestyling.ixiaoshuo.utils;

import android.util.Log;

import java.io.RandomAccessFile;

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

    public static Encoding determineEncoding(RandomAccessFile file) {
        Encoding enc = Encoding.GBK;
        try {
            file.seek(0);
            if (file.length() < 3) return enc;
            byte[] bom = new byte[3]; //byte order mark
            file.read(bom);

            if ((bom[0] & 0XFF) == 0xFF && (bom[1] & 0XFF) == 0xFE)
                enc = Encoding.UTF16LE;
            else if ((bom[0] & 0XFF) == 0xFE && (bom[1] & 0XFF) == 0xFF)
                enc = Encoding.UTF16BE;
            else if ((bom[0] & 0XFF) == 0xEF && (bom[1] & 0XFF) == 0xBB && (bom[2] & 0XFF) == 0xBF)
                enc = Encoding.UTF8;
            else {//test if the file is encoded using GBK or BIG5 character set
                int gbkCount = 0;
                int big5Count = 0;
                int utf16leCount = 0;
                int utf16beCount = 0;
                int utf8Count = 0;

                file.seek(0);
                byte[] bs = new byte[4096];
                file.read(bs);
                int len = bs.length - 2;
                //look up the Chinese characters "的"
                for (int i = 0; i < len; ++i) {
                    if ((bs[i] & 0xFF) == 0xB5 && (bs[i + 1] & 0xFF) == 0xC4) {
                        ++gbkCount;
                        ++i;
                    } else if ((bs[i] & 0xFF) == 0xE7 && (bs[i + 1] & 0xFF) == 0x9A && (bs[i + 2] & 0xFF) == 0x84) {
                        ++utf8Count;
                        i += 2;
                    } else if ((bs[i] & 0xFF) == 0x84 && (bs[i + 1] & 0xFF) == 0x76) {
                        ++utf16leCount;
                        ++i;
                    } else if ((bs[i] & 0xFF) == 0x76 && (bs[i + 1] & 0xFF) == 0x84) {
                        ++utf16beCount;
                        ++i;
                    } else if ((bs[i] & 0xFF) == 0xAA && (bs[i + 1] & 0xFF) == 0xBA) {
                        ++big5Count;
                        ++i;
                    }
                }

                if (gbkCount > utf8Count && gbkCount > big5Count && gbkCount > utf16leCount && gbkCount > utf16beCount)
                    enc = Encoding.GBK;
                else if (utf8Count > gbkCount && utf8Count > big5Count && utf8Count > utf16leCount && utf8Count > utf16beCount)
                    enc = Encoding.UTF8;
                else if (utf16leCount > gbkCount && utf16leCount > big5Count && utf16leCount > utf8Count && utf16leCount > utf16beCount)
                    enc = Encoding.UTF16LE;
                else if (utf16beCount > gbkCount && utf16beCount > big5Count && utf16beCount > utf16leCount && utf16beCount > utf16leCount)
                    enc = Encoding.UTF16BE;
                else if (big5Count > gbkCount && big5Count > utf8Count && big5Count > utf16leCount && big5Count > utf16beCount)
                    enc = Encoding.BIG5;
            }
        } catch (Exception ex) {
            Log.e("File ERROR", "encoding detection failed.");
        }
        return enc;
    }

}
