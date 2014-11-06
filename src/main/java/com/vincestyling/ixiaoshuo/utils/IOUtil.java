package com.vincestyling.ixiaoshuo.utils;

import com.vincestyling.ixiaoshuo.pojo.Book;

import java.io.*;

/**
 * Most of the functions herein are re-implementations of the ones in apache io
 * IOUtils. The reason for re-implementing this is that the functions are fairly
 * simple and using my own implementation saves the inclusion of a 200Kb jar file.
 */
public class IOUtil {
    public static final int IO_COPY_BUFFER_SIZE = 1024 * 4;

    /**
     * Returns the contents of the InputStream as a byte[]
     */
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        copy(in, result);
        result.flush();
        in.close();
        return result.toByteArray();
    }

    /**
     * if totalNrRead < 0 then totalNrRead is returned, if (nrRead +
     * totalNrRead) < Integer.MAX_VALUE then nrRead + totalNrRead is returned,
     * -1 otherwise.
     */
    public static int calcNewNrReadSize(int nrRead, int totalNrNread) {
        if (totalNrNread < 0) return totalNrNread;
        if (totalNrNread > (Integer.MAX_VALUE - nrRead)) return -1;
        return (totalNrNread + nrRead);
    }

    /**
     * Copies the contents of the InputStream to the OutputStream.
     *
     * @return the nr of bytes read, or -1 if the amount > Integer.MAX_VALUE
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[IO_COPY_BUFFER_SIZE];
        int readSize;
        int result = 0;
        while ((readSize = in.read(buffer)) >= 0) {
            out.write(buffer, 0, readSize);
            result = calcNewNrReadSize(readSize, result);
        }
        out.flush();
        return result;
    }

    public static boolean deleteDirectory(File dir) {
        // why should I rename before delete : http://stackoverflow.com/a/11776458/1294681
        File newDir = new File(dir.getAbsolutePath() + System.currentTimeMillis());
        dir.renameTo(newDir);

        File[] bookFiles = newDir.listFiles();
        for (File bookFile : bookFiles) {
            if (bookFile.isDirectory()) deleteDirectory(bookFile);
            bookFile.delete();
        }
        return newDir.delete();
    }

    public static void deleteBookCache(Book book) {
        try {
            deleteDirectory(Paths.getCacheDirectorySubFolder(book.getId()));
            File coverFile = new File(book.getLocalCoverPath());
            coverFile.delete();
        } catch (Exception ex) {}
    }

    public static boolean saveBookChapter(int bookId, int chapterId, String content) {
        if (StringUtil.isNotEmpty(content)) {
            String fileName = String.valueOf(chapterId);
            File chapterFile = new File(Paths.getCacheDirectorySubFolder(bookId), fileName);
            try {
                FileOutputStream contentOutput = new FileOutputStream(chapterFile);
                contentOutput.write(content.getBytes(Encoding.GBK.getName()));
                contentOutput.close();
                return true;
            } catch (IOException e) {
                AppLog.e(e.getMessage(), e);
            }
        }
        return false;
    }

}
