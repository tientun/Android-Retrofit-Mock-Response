package com.tientun.mockresponse;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by TienHN on 7/19/16.
 */
public class AssetsFileScanner {

    private Context mContext;

    public AssetsFileScanner(Context context) {
        mContext = context;
        listAssetFiles("mock.api");
    }


    private boolean listAssetFiles(String path) {
        String[] list;
        try {
            list = mContext.getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (path.isEmpty()) {
                        listAssetFiles(file);
                    } else {
                        listAssetFiles(path + "/" + file);
                    }
                }
            } else {
                Log.d("xxx", path);
            }
        } catch (IOException e) {
            Log.e("xxx", e.getMessage(), e);
            return false;
        }
        return true;
    }
}
