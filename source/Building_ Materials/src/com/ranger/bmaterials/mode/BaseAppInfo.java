package com.ranger.bmaterials.mode;

import java.text.Collator;

import com.ranger.bmaterials.netresponse.BaseResult;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseAppInfo /*extends BaseResult*/ implements Comparable<BaseAppInfo> {

	private final Collator sCollator = Collator.getInstance();

	private String packageName; //1
	private String name;//2
	
	
	/*private String version ;
	private int versionInt ;
	private String extara ;
	private long publishDate ;*/
	
	/*
	WhiteListTable.COLUMN_NAME,
	WhiteListTable.COLUMN_PKG_NAME,WhiteListTable.COLUMN_PUBLISH_DATE,
	WhiteListTable.COLUMN_SIGN,WhiteListTable.COLUMN_VERSION,
	WhiteListTable.COLUMN_VERSION_INT,WhiteListTable.COLUMN_EXTRA,
	
	*/
	

	public BaseAppInfo() {
		super();
	}



	public BaseAppInfo(String packageName, String name) {
		super();
		this.packageName = packageName;
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public int compareTo(BaseAppInfo another) {
		return sCollator.compare(getName(), another.getName());
	}

//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeString(name);
//		dest.writeString(packageName);
//	}
//
//	public static final Parcelable.Creator<BaseAppInfo> CREATOR = new Parcelable.Creator<BaseAppInfo>() {
//
//		@Override
//		public BaseAppInfo createFromParcel(Parcel source) {
//			BaseAppInfo appInfo = new BaseAppInfo();
//			appInfo.setName(source.readString());
//			appInfo.setPackageName(source.readString());
//			return appInfo;
//		}
//
//		@Override
//		public BaseAppInfo[] newArray(int size) {
//			return new BaseAppInfo[size];
//		}
//
//	};


	@Override
	public String toString() {
		return "BaseAppInfo [packageName=" + packageName + ", name=" + name
				+ "]";
	}


}
