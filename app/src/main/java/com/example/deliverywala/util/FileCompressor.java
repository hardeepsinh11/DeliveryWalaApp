package com.example.deliverywala.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import io.reactivex.Flowable;
import java.io.File;
import java.io.IOException;

public class FileCompressor {
    private int maxWidth = 612;
    private int maxHeight = 816;
    private CompressFormat compressFormat = CompressFormat.JPEG;
    private int quality = 25;
    private String destinationDirectoryPath;

    public FileCompressor(Context context) {
        this.destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";
    }

    public FileCompressor setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public FileCompressor setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public FileCompressor setCompressFormat(CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
        return this;
    }

    public FileCompressor setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public FileCompressor setDestinationDirectoryPath(String destinationDirectoryPath) {
        this.destinationDirectoryPath = destinationDirectoryPath;
        return this;
    }

    public File compressToFile(File imageFile) throws IOException {
        return compressToFile(imageFile, imageFile.getName());
    }

    public File compressToFile(File imageFile, String compressedFileName) throws IOException {
        return ImageUtil.compressImage(
            imageFile, maxWidth, maxHeight, compressFormat, quality,
            destinationDirectoryPath + File.separator + compressedFileName
        );
    }

    public Bitmap compressToBitmap(File imageFile) throws IOException {
        return ImageUtil.decodeSampledBitmapFromFile(imageFile, maxWidth, maxHeight);
    }

    public Flowable<File> compressToFileAsFlowable(File imageFile) {
        return compressToFileAsFlowable(imageFile, imageFile.getName());
    }

    public Flowable<File> compressToFileAsFlowable(File imageFile, String compressedFileName) {
        return Flowable.defer(() -> {
            try {
                return Flowable.just(compressToFile(imageFile, compressedFileName));
            } catch (IOException e) {
                return Flowable.error(e);
            }
        });
    }

    public Flowable<Bitmap> compressToBitmapAsFlowable(File imageFile) {
        return Flowable.defer(() -> {
            try {
                return Flowable.just(compressToBitmap(imageFile));
            } catch (IOException e) {
                return Flowable.error(e);
            }
        });
    }
}