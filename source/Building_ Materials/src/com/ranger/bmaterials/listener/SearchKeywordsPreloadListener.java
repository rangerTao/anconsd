package com.ranger.bmaterials.listener;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.mode.KeywordsList;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;

public class SearchKeywordsPreloadListener implements IRequestListener {

	@Override
	public void onRequestSuccess(BaseResult responseData) {

		if (responseData.getErrorCode() == DcError.DC_OK) {
			int tag = StringUtil.parseInt(responseData.getTag());
			if (tag == Constants.NET_TAG_KEYWORDS) {
				Constants.keywordsListForSearch = (KeywordsList) responseData;
			} else if (tag == Constants.NET_TAG_SEARCH) {
			}

		} else {
		}

	}

	@Override
	public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {

	}

}
