package com.photos.util;

import android.content.Context;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Help copy (photo) files from one source to destination.
 * Modified from
 * <a href="https://androidfortechs.blogspot.com/2020/12/how-to-convert-uri-to-file-android-10.html">An AndroidForTechs blog</a>.
 */
public class PhotosFileUtils {

    public static String getFileName(Context context, Uri uri) {
        if (Objects.equals(uri.getScheme(), "content")) { /* Resolve potential symlinks */
            try (var returnCursor = context.getContentResolver().query(uri, null, null, null, null)) {
                assert returnCursor != null;
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                return returnCursor.getString(nameIndex);
            }
        } else {
            return uri.getLastPathSegment();
        }
    }

    /**
     * Checks if we can add the file to local storage, if there exists a file increment it like so
     * photo.png -> photo-1.png -> photo-2.png -> ...
     * @param context The context of our activity.
     * @param sourceFileName The source filename.
     * @return A free destination filename we can use without conflict
     */
    public static Pair<String, File> getDest(Context context, String sourceFileName) {
        String fileName = sourceFileName;
        File dest = new File(context.getFilesDir().getPath() + File.separatorChar + fileName);

        int fileNameExtensionStart = fileName.lastIndexOf('.');
        String fileNamePart = fileName.substring(0, fileNameExtensionStart);
        String fileExtensionPart = fileName.substring( fileNameExtensionStart + 1);

        for (int i = 1; dest.exists(); i++) {
            fileName = fileNamePart + "-" + i + "." + fileExtensionPart;
            dest = new File(context.getFilesDir().getPath() + File.separatorChar + fileName);
        }

        return new Pair<>(fileName, dest);
    }

    /**
     * Copies a file from a URI to local storage.
     * @param context The context of our activity
     * @param sourceUri The source URI we want to copy
     * @param destFileName The destination file name, if conflicts will overwrite
     */
    public static void copyFileToLocal(Context context, Uri sourceUri, String destFileName) {
        /*
         * We will use Buffered Streams despite overhead on small files for a few reasons
         * 1. In the event of large files
         * 2. We will not read/write many files at once so the overhead is negligible
         */
        try (
            BufferedInputStream bis = new BufferedInputStream(context.getContentResolver().openInputStream(sourceUri));
            BufferedOutputStream bos = new BufferedOutputStream(context.openFileOutput(destFileName, Context.MODE_PRIVATE))
        ) {
            byte[] buf = new byte[1024];
            while (bis.read(buf) > 0) {
                bos.write(buf);
            }
            bos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
