package com.ranger.bmaterials.mode;

/**
 * Created by taoliang on 14-6-17.
 */
public class PackageSizeInfo {

    //数据所占空间
    private long dataSize;
    //缓存空间
    private long cacheSize;
    //安装文件空间
    private long codeSize;
    //包名
    private String pkgname;
    //是否成功
    private boolean successed;

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(long codeSize) {
        this.codeSize = codeSize;
    }

    public String getPkgname() {
        return pkgname;
    }

    public void setPkgname(String pkgname) {
        this.pkgname = pkgname;
    }

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }
}
