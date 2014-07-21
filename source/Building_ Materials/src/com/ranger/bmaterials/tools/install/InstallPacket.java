package com.ranger.bmaterials.tools.install;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.PackageInstallerCallback;

public class InstallPacket implements Parcelable ,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7840755427972524819L;
	String name ;
	String packageName;
	String filepath;
	boolean started ;
	long downloadId;
	String gameId ;
	String downloadUrl ;
	
	
	InstallStatus status ;
	int errorReason ;
	
	
	PackageInstallerCallback callback ;
	public InstallPacket() {
		super();
	}
	public InstallPacket(String name,String packageName, String filepath,long downloadId,String gameId,String downloadUrl) {
		super();
		this.name = name ;
		this.packageName = packageName;
		this.downloadId = downloadId ;
		this.filepath = filepath;
		this.started = false ;
		this.gameId = gameId ;
		this.downloadUrl = downloadUrl ;
	}
	
	
	public int getErrorReason() {
		return errorReason;
	}
	public void setErrorReason(int errorReason) {
		this.errorReason = errorReason;
	}
	public InstallStatus getStatus() {
		return status;
	}
	public void setStatus(InstallStatus status) {
		this.status = status;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public boolean isStarted() {
		return started;
	}
	public void setStarted(boolean started) {
		this.started = started;
	}
	
	public long getDownloadId() {
		return downloadId;
	}
	public void setDownloadId(long downloadId) {
		this.downloadId = downloadId;
	}
	public PackageInstallerCallback getCallback() {
		return callback;
	}
	public void setCallback(PackageInstallerCallback callback) {
		this.callback = callback;
	}
	
	
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filepath == null) ? 0 : filepath.hashCode());
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false ;
		}
		if (this == obj){
			return true;
		}
		InstallPacket other = (InstallPacket) obj;
		if(other.packageName.equals(this.packageName)){
			return true ;
		}
		return false;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "InstallPacket [packageName=" + packageName + ", filepath="
				+ filepath + ", started=" + started + ", callback="
				+ callback + "]";
	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.packageName);
		dest.writeLong(downloadId);
		dest.writeString(this.filepath);
		dest.writeString(gameId);
		dest.writeString(downloadUrl);
		dest.writeSerializable(status);
		boolean boolArr[] ={this.started};
		dest.writeBooleanArray(boolArr);
		
	}
	public static final Parcelable.Creator<InstallPacket> CREATOR = new Creator<InstallPacket>() {

		public InstallPacket createFromParcel(Parcel source) {
			InstallPacket pack = new InstallPacket();
			pack.name = source.readString();
			pack.packageName = source.readString();
			pack.downloadId = source.readLong();
			pack.filepath = source.readString();
			pack.gameId = source.readString();
			pack.downloadUrl = source.readString();
			pack.status = (InstallStatus) source.readSerializable();
			boolean boolArr[] = new boolean[1];
			source.readBooleanArray(boolArr);
			pack.started = boolArr[0];
			return pack;
		}

		public InstallPacket[] newArray(int size) {
			return new InstallPacket[size];
		}

	};
}