package com.ranger.bmaterials.mode;

import android.os.Parcel;
import android.os.Parcelable;

/**
	 * 唯一标识一个app
	 * @author wangliang
	 *
	 */
	public  class PackageMark implements  Parcelable {
		/**
		 * game id
		 */
		public String gameId;
		public String packageName;
		public String version;
		public int versionCode;
		public boolean isDiffUpdate ;
		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(gameId);
			dest.writeString(packageName);
			dest.writeString(version);
			dest.writeInt(versionCode);
			dest.writeInt(isDiffUpdate?1:0);
		}

		public static final Parcelable.Creator<PackageMark> CREATOR = new Parcelable.Creator<PackageMark>() {

			@Override
			public PackageMark createFromParcel(Parcel source) {
				PackageMark mode = new PackageMark();
				mode.gameId = source.readString();
				mode.packageName = source.readString();
				mode.version = source.readString();
				mode.versionCode = source.readInt();
				mode.isDiffUpdate = (source.readInt() == 1);
				return mode;
			}
			
			

			@Override
			public PackageMark[] newArray(int size) {
				return new PackageMark[size];
			}

		};

	}
	