package com.ranger.bmaterials.obverser;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.os.RemoteException;

import com.ranger.bmaterials.mode.PackageSizeInfo;

/**
 * Created by taoliang on 14-6-17.
 */
public class PackageSizeObverser extends IPackageStatsObserver.Stub {

    long dataSize;
    long cacheSize;
    long codeSize;
    boolean success;

    @Override
    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {

        dataSize = pStats.dataSize;
        cacheSize = pStats.cacheSize;
        codeSize = pStats.codeSize;

        success = succeeded;
    }

    public PackageSizeInfo getPackageSizeInfo(){
        PackageSizeInfo psi = new PackageSizeInfo();

        psi.setCacheSize(cacheSize);
        psi.setDataSize(dataSize);
        psi.setCodeSize(codeSize);
        psi.setSuccessed(success);

        return psi;
    }
}
