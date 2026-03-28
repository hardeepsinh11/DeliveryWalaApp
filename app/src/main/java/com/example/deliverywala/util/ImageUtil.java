package com.example.deliverywala.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    public static File compressImage(
        File imageFile, int reqWidth, int reqHeight,
        CompressFormat compressFormat, int quality, String destinationPath
    ) throws IOException {
        FileOutputStream fileOutputStream = null;
        File file = new File(destinationPath).getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            fileOutputStream = new FileOutputStream(destinationPath);
            decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight).compress(
                compressFormat, quality,
                fileOutputStream
            );
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        return new File(destinationPath);
    }

    public static Bitmap decodeSampledBitmapFromFile(File imageFile, int reqWidth, int reqHeight) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        
        Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        Matrix matrix = new Matrix();
        
        if (orientation == 6) {
            matrix.postRotate(90f);
        } else if (orientation == 3) {
            matrix.postRotate(180f);
        } else if (orientation == 8) {
            matrix.postRotate(270f);
        }
        
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(),
            matrix, true
        );
        return scaledBitmap;
    }

    private static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth,
        int reqHeight
    ) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}