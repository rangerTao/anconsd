package com.andconsd.framework.entry;

public class DkKeyString {
	private String key;
	private boolean valueEncrypted; // ������value�Ƿ񾭹���ܣ�Ĭ��Ϊfalse

	public boolean isValueEncrypted() {
		return valueEncrypted;
	}

	public void setValueEncrypted(boolean valueEncrypted) {
		this.valueEncrypted = valueEncrypted;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
