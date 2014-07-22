package com.ranger.bmaterials.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.mode.KeywordsList;
import com.ranger.bmaterials.netresponse.BMCompanyInfoResult;
import com.ranger.bmaterials.netresponse.BMProductInfoResult;
import com.ranger.bmaterials.netresponse.BMProvinceListResult;
import com.ranger.bmaterials.netresponse.BMSearchResult;
import com.ranger.bmaterials.netresponse.BMUserLoginResult;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.CheckUpdateResult;
import com.ranger.bmaterials.netresponse.UserNameRegisterResult;
import com.ranger.bmaterials.tools.DateUtil;
import com.ranger.bmaterials.tools.StringUtil;

/**
 * 
 * @author wenzutong
 * 
 */
public class JSONParser {


	// 用户注册
		public static BaseResult parseBMUserNameRegister(String resData) {

			UserNameRegisterResult result = new UserNameRegisterResult();

			do {
				try {

					JSONObject jsonObj = new JSONObject(resData);

					String tag = jsonObj.getString(Constants.JSON_TAG);
					int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
					String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);

					result.setTag(tag);
					result.setErrorCode(errorcode);
					result.setErrorString(errorStr);

					if (DcError.DC_OK != errorcode) {
						break;
					}

					String username = jsonObj.getString(Constants.JSON_USERNAME);
					String userid = jsonObj.getString(Constants.JSON_USERID);
					int registertype = jsonObj.getInt(Constants.JSON_REGISTERTYPE);
					String sessionid = jsonObj.getString(Constants.JSON_SESSIONID);
					String nickname = jsonObj.getString(Constants.JSON_NICKNAME);

					result.setUsername(username);
					result.setUserid(userid);
					result.setRegistertype(registertype);
					result.setSessionid(sessionid);
					result.setNickname(nickname);

				} catch (Exception e) {
					result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
					result.setErrorString("Json Parser Error");
				}

			} while (false);

			return result;

		}

	
	// 得到手机验证码
	public static BaseResult parseBMPhoneVerifyCode(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}


	// 修改密码
	public static BaseResult parseChangePwd(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}


	// 删除消息、设置为已读
	public static BaseResult parseDeleteMessage(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}
			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}
		} while (false);
		return result;
	}


	// 检查更新
	public static BaseResult parseCheckUpdate(String resData) {
		CheckUpdateResult result = new CheckUpdateResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

				result.updatetype = jsonObj.getInt(Constants.JSON_APP_UPDATETYPE);
				result.apkurl = jsonObj.getString(Constants.JSON_APP_APKURL);
				result.apkversion = jsonObj.getString(Constants.JSON_APP_APKVERSION);
				result.apksize = jsonObj.getString(Constants.JSON_APP_APKSIZE);
				result.description = jsonObj.getString(Constants.JSON_APP_DESCRIPTION);

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

	// 用户反馈
	public static BaseResult parseFeedback(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}


	static class CommonResp {
		int errorCode;
		String errorString;
		String tag;
	}

	static CommonResp pareseCommonResp(String resData) throws JSONException {
		JSONObject outterObj = new JSONObject(resData);
		CommonResp commonResp = new CommonResp();
		commonResp.tag = outterObj.getString(Constants.JSON_TAG);
		try {
			commonResp.errorCode = outterObj.getInt(Constants.JSON_ERROR_CODE);
			commonResp.errorString = outterObj.getString(Constants.JSON_ERROR_MSG);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return commonResp;
	}

	// 手机号一键注册登陆
	public static BaseResult parseChangeNickname(String resData) {
		BaseResult result = new BaseResult();
		do {
			try {
				JSONObject jsonObj = new JSONObject(resData);
				int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
				String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
				String tag = jsonObj.getString(Constants.JSON_TAG);

				result.setTag(tag);
				result.setErrorCode(errorcode);
				result.setErrorString(errorStr);

				if (DcError.DC_OK != errorcode) {
					break;
				}

			} catch (JSONException e) {
				result.setErrorCode(DcError.DC_JSON_PARSER_ERROR);
				result.setErrorString("Json Parser Error");
			}

		} while (false);
		return result;
	}

    /************************************************************************************************/

    /**
     * BmUser
     */
    public static BMUserLoginResult parserBMUserLoginResult(String res){

        BMUserLoginResult result = null;
        Gson gson = new Gson();
        try{
            result = gson.fromJson(res,BMUserLoginResult.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    /**
     * tag 241 获取搜索关键字
     */
    public static BaseResult parseBMKeywords(String resData) {
        KeywordsList keywordsList = KeywordsList.getInstance();
        try {

            JSONArray outter = new JSONArray(resData);
            int length = outter.length();
            List<String> dataList = new ArrayList<String>(length);
            for (int i = 0; i < length; i++) {
                String keyword = outter.getString(i);
                dataList.add(keyword);
            }
            keywordsList.setKeywords(dataList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return keywordsList;
    }

    public static BaseResult parseBMProvinceList(String res){
        BMProvinceListResult result = new BMProvinceListResult();
        try{
            JSONArray jsonArray = new JSONArray(res);
            for(int i=0;i<jsonArray.length();i++){
                String item = jsonArray.getString(i);
                result.addItem(item);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public static BaseResult parseBMProductInfo(String res){
        BMProductInfoResult result = new BMProductInfoResult();
        try{
            Gson gson = new Gson();

            result = gson.fromJson(res,BMProductInfoResult.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    /**
     * tag 242 根据关键字搜索游戏
     */
    public static BaseResult parseBMSearchProducts(String resData) {
        BMSearchResult searchResult = new BMSearchResult();
        try {

                JSONObject outterObj = new JSONObject(resData);
                JSONArray jsonList = outterObj.getJSONArray(Constants.BM_JSON_DATA_LIST);
                int length = jsonList.length();
                ArrayList<BMSearchResult.BMSearchData> dataList = new ArrayList<BMSearchResult.BMSearchData>(length);
                for (int i = 0; i < length; i++) {
                    try{

                        Gson gson = new Gson();
                        BMSearchResult.BMSearchData bmsd = gson.fromJson(jsonList.getJSONObject(i).toString(),BMSearchResult.BMSearchData.class);

                        dataList.add(bmsd);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }

            searchResult.setDataList(dataList);
            searchResult.setTotal(outterObj.getInt(Constants.JSON_SEARCH_TOTAL_COUNT));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * BmUser
     */
    public static BMCompanyInfoResult parserBMComInfoResult(String res){

        BMCompanyInfoResult result = null;
        Gson gson = new Gson();
        try{
            result = gson.fromJson(res,BMCompanyInfoResult.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }
}
