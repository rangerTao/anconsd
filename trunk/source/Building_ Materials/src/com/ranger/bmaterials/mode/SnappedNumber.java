package com.ranger.bmaterials.mode;

import com.ranger.bmaterials.netresponse.BaseResult;

public class SnappedNumber extends BaseResult{
	private String number ;
	private String gameId ;
	private String grabId;
	private int resCode ;
	
	
	//0:抢号成功;1:没中号;2:被抢光了;3:重复抢号(需要将grabid和grabbednumber返回);4:还没开始;5:已经结束;其他数字:其他错误
	public static class ResCode{
		
		public static final int SUCCESS = 0;
		public static final int BAD_LUCK = 1;
		public static final int NONE = 2;
		public static final int MULTIPLE_ACTION  = 3;
		public static final int PENDING  = 4;
		public static final int OVER  = 5;
		public static final int OTHER_ERROR  = 6;
		
	}
	public SnappedNumber() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SnappedNumber(String number, String gameId, String grabId) {
		super();
		this.number = number;
		this.gameId = gameId;
		this.grabId = grabId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGrabId() {
		return grabId;
	}

	public void setGrabId(String grabId) {
		this.grabId = grabId;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}
	
}
