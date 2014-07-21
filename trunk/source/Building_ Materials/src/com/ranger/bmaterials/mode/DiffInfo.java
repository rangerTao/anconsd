package com.ranger.bmaterials.mode;

import android.os.Parcel;
import android.os.Parcelable;

public class DiffInfo implements Parcelable{

	public PackageMode packageMode;

	public boolean success;// 合成补丁的结果状态
	public int failedReason ;

	public String patchPath;//补丁路径 ps:源包路径为已安装应用的apk路径 补丁 输出路径重命名为补丁路径

	
	public DiffInfo() {
	}
	
	public DiffInfo(PackageMode packageMode) {
		this.packageMode = packageMode;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(success?1:0);
		dest.writeInt(failedReason);
		dest.writeString(patchPath);
		dest.writeParcelable(packageMode, PARCELABLE_WRITE_RETURN_VALUE);
	}
	
	
	
	public static final Parcelable.Creator<DiffInfo> CREATOR = new Parcelable.Creator<DiffInfo>() {

		@Override
		public DiffInfo createFromParcel(Parcel source) {
			DiffInfo mode = new DiffInfo();
			mode.success = (source.readInt()==1);
			mode.failedReason = (source.readInt());
			mode.patchPath= source.readString();
			mode.packageMode = source.readParcelable(PackageMode.class.getClassLoader());
			return mode;
		}
		
		

		@Override
		public DiffInfo[] newArray(int size) {
			return new DiffInfo[size];
		}

	};

}
