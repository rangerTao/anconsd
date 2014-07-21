package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;
import java.util.List;

public class ExchangeHistoryDetailResult extends BaseResult {

	ArrayList<ExchangeItem> data;

	private int totalCount;

	public ArrayList<ExchangeItem> getData() {
		return data;
	}

	public void setData(ArrayList<ExchangeItem> data) {
		this.data = data;
	}

	public final int getTotalCount() {
		return totalCount;
	}

	public final void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public static class ExchangeItem {

		public ExchangeItem(String exchangeid, String propid, String propicon, String date, String expire, int metatype, String cardnum, String password,
				int operator) {
			super();
			this.exchangeid = exchangeid;
			this.propid = propid;
			this.propicon = propicon;
			this.date = date;
			this.expire = expire;
			this.metatype = metatype;
			this.cardnum = cardnum;
			this.password = password;
			this.operator = operator;
		}

		public String exchangeid; // string 兑换id 必选
		public String propid; // string 道具id 必选
		public String propicon; // string 道具图标 必选
		public String date; // string 兑换日期 必选
		public String expire; // string 过期日期 必选
		// public String metadata; object 附属信息（包含下面四个字段） 必选
		public int metatype; // uint32 数据类型 1：充值卡 必选
		public String cardnum; // string 充值卡号 必选
		public String password; // string 充值密码 必选
		public int operator; // uint32 运营商: 1 中国移动；2 中国联通；3 中国电信 必选

		public String getExchangeid() {
			return exchangeid;
		}

		public void setExchangeid(String exchangeid) {
			this.exchangeid = exchangeid;
		}

		public String getPropid() {
			return propid;
		}

		public void setPropid(String propid) {
			this.propid = propid;
		}

		public String getPropicon() {
			return propicon;
		}

		public void setPropicon(String propicon) {
			this.propicon = propicon;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getExpire() {
			return expire;
		}

		public void setExpire(String expire) {
			this.expire = expire;
		}

		public int getMetatype() {
			return metatype;
		}

		public void setMetatype(int metatype) {
			this.metatype = metatype;
		}

		public String getCardnum() {
			return cardnum;
		}

		public void setCardnum(String cardnum) {
			this.cardnum = cardnum;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public int getOperator() {
			return operator;
		}

		public void setOperator(int operator) {
			this.operator = operator;
		}

	}
}
