package com.example.socketiodemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private static String TAG = "com.example.socketiodemo.FileUtils";

    public static String saveAssetsFileToStorage(Context context, String assetsFileName) {
        Log.i(TAG, "saveAssetsFileToStorage: enter file:" + assetsFileName);
        String savePath = "";
        if (context == null || TextUtils.isEmpty(assetsFileName)) {
            return savePath;
        }

        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(assetsFileName);
            FileOutputStream outputStream;
            outputStream = context.openFileOutput(assetsFileName, Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            String path = context.getFilesDir().getAbsolutePath() + "/" + assetsFileName;
            Log.i("TAG", "saveAssetsFileToStorage: " + path);
            if (new File(path).exists()) {
                savePath = path;
            }
        } catch (IOException e) {
            Log.w("error", "saveAssetsFileToStorage: ", e);
        }finally {
            assetManager.close();
        }
        return savePath;
    }
}
