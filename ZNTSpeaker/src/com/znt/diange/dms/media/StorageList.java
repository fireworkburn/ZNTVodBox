package com.znt.diange.dms.media;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

public class StorageList 
{
	private Context mActivity;
    private StorageManager mStorageManager;
    private Method mMethodGetPaths;
    /**
     * total space
     */
    public static final int TOTALSPACE = 0;
    /**
     * free space
     */
    public static final int FREESPACE = 1;
 
    public StorageList(Context activity) {
        mActivity = activity;
        if (mActivity != null) {
            mStorageManager = (StorageManager) mActivity
                    .getSystemService(Activity.STORAGE_SERVICE);
 
            try {
                mMethodGetPaths = mStorageManager.getClass().getMethod(
                        "getVolumePaths");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
 
    public String[] getVolumePathsFor14() {
        String[] paths = null;
        try {
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (IllegalArgumentException e) {
 
        } catch (IllegalAccessException e) {
 
        } catch (InvocationTargetException e) {
 
        } catch (Exception e) {
 
        }
        return paths;
    }
 
    public String[] getVolumePaths() {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            return getVolumePathsFor14();
        } else if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return new String[] { Environment.getExternalStorageDirectory()
                    .getAbsolutePath() };
        }
        return null;
    }
    /**
     * @param type, see {@link #TOTALSPACE}, or {@link #TOTALSPACE}.
     * @return
     */
    public String getBestVolumePaths(int type) {
        String[] vps = getVolumePaths();
        if (vps == null)
            return null;
        if (vps.length == 1)
            return vps[0];
        File file = new File(vps[0]);
        if (type == TOTALSPACE) {
            for (int i = 1; i < vps.length; i++) {
                File file2 = new File(vps[i]);
                if (file2.getTotalSpace() > file.getTotalSpace())
                    file = file2;
            }
        } else if (type == FREESPACE) {
            for (int i = 1; i < vps.length; i++) {
                File file2 = new File(vps[i]);
                if (file2.getFreeSpace() > file.getFreeSpace())
                    file = file2;
            }
        }
        return file.getAbsolutePath();
    }
}
