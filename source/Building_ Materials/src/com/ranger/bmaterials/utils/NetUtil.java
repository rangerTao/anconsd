package com.ranger.bmaterials.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;

import com.ranger.bmaterials.app.BMApplication;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.encrypt.AES;
import com.ranger.bmaterials.json.JSONBuilder;
import com.ranger.bmaterials.json.JSONManager;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.net.IHttpInterface;
import com.ranger.bmaterials.net.INetListener;
import com.ranger.bmaterials.net.NetManager;
import com.ranger.bmaterials.netresponse.BaseResult;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class NetUtil implements INetListener {
    /**
     * 单例
     */
    private static NetUtil mInstance;
    private IHttpInterface mHttpIml;

    /**
     * 存储Listener
     */
    private SparseArray<IRequestListener> mObservers = new SparseArray<IRequestListener>();

    /**
     * 当前requestId
     */
    private int mCurrentRequestId;

    /**
     * 构造器
     */
    private NetUtil() {
        mHttpIml = NetManager.getHttpConnect();
    }

    public static NetUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NetUtil();
        }

        return mInstance;
    }

    // 取消请求
    public void cancelRequestById(int requestId) {
        mHttpIml.cancelRequestById(requestId);
    }


    /**
     * ` 检查更新
     */
    public int requestCheckUpdate(IRequestListener observer) {
        mCurrentRequestId = mHttpIml.sendRequest("", Constants.NET_TAG_CHECK_UPDATE, JSONManager.getJsonBuilder().buildCheckUpdateString(), this);

        addObserver(mCurrentRequestId, observer);
        return mCurrentRequestId;
    }

    private void addObserver(int key, IRequestListener observer) {
        mObservers.put(key, observer);
    }

    private void removeObserver(int key) {
        mObservers.remove(key);
    }

    // ----------------------------------INetListener接口实现---------------------------------------------
    @Override
    public void onNetResponse(int requestTag, BaseResult responseData, int requestId) {
        // TODO Auto-generated method stub
        IRequestListener _listener = mObservers.get(requestId);
        if (_listener != null) {
            responseData.setRequestID(requestId);
            _listener.onRequestSuccess(responseData);
        }
        removeObserver(requestId);
    }

    @Override
    public void onDownLoadStatus(DownLoadStatus status, int requestId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDownLoadProgressCurSize(long curSize, long totalSize, int requestId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNetResponseErr(int requestTag, int requestId, int errorCode, String msg) {
        // TODO Auto-generated method stub
        IRequestListener _listener = (IRequestListener) mObservers.get(requestId);
        if (_listener != null) {
            _listener.onRequestError(requestTag, requestId, errorCode, msg);
        }
        removeObserver(requestId);
    }

    // ----------------------------------------END----------------------------------------------

    /**
     * 请求回调接口
     */
    public interface IRequestListener {

        void onRequestSuccess(BaseResult responseData);

        void onRequestError(int requestTag, int requestId, int errorCode, String msg);

    }

    /**
     * *************************************************************************************************
     */

    // 命名空间
    String nameSpace = "http://mapi.jc.net.cn";

    // EndPoint
    String endPoint = "http://mapi.jc.net.cn/JcMobileService";
    // SOAP Action
    String soapAction = "http://mapi.jc.net.cn/";

    /**
     * 用户登录
     */
    public int requestUserLogin(String username, String password, final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "checkLogin";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("info", JSONBuilder.buildLoginString(username, password));

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);


        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parserBMUserLoginResult(result);
                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    observer.onRequestSuccess(baseResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(0, mCurrentRequestId, 1001, "登录出错，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * 手机号验证码
     */
    public int requestPhoneVerifyCode(String phonenum, int flag, final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "getSmsCode";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("in0", AES.getInstance().encrypt(phonenum));

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBMPhoneVerifyCode(result);
                    baseResult.setTag(Constants.NET_TAG_GET_PHONE_VERIFYCODE + "");

                    Log.e("TAG", "webservice result " + result);

                    if (baseResult.getErrorCode() == 1) {
                        observer.onRequestSuccess(baseResult);
                    } else {
                        observer.onRequestError(Constants.NET_TAG_GET_PHONE_VERIFYCODE, mCurrentRequestId, 1001, baseResult.getErrorString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_GET_PHONE_VERIFYCODE, mCurrentRequestId, 1001, "请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * 手机号注册
     */
    public int requestPhoneumRegister(String username, String password, String telephone, String verifyCode, final IRequestListener observer) {
        // 调用的方法名称
        String methodName = "addUser";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("info", JSONBuilder.buildAddUserString(username, password, telephone, verifyCode));

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);


        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBMUserNameRegister(result);
                    baseResult.setTag(Constants.NET_TAG_USERNAME_REGISTER + "");

                    Log.e("TAG", "webservice result " + result);

                    if (baseResult.getErrorCode() == 1)
                        observer.onRequestSuccess(baseResult);
                    else
                        observer.onRequestError(Constants.NET_TAG_USERNAME_REGISTER, mCurrentRequestId, 1001, baseResult.getErrorString());

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_USERNAME_REGISTER, mCurrentRequestId, 1001, "请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }


    /**
     * 获取搜索关键字 tag = 241
     *
     * @param observer
     * @return
     */
    public int requestForKeywords(int count, final IRequestListener observer,Context context) {

        // 调用的方法名称
        String methodName = "getHotWord";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        final SharedPreferences sp = context.getSharedPreferences("cache", Context.MODE_WORLD_WRITEABLE);

        final String cache_province = sp.getString("keywords", "");

        if(!cache_province.equals("")){
            BaseResult baseResult = JSONParser.parseBMKeywords(cache_province);
            baseResult.setTag(Constants.NET_TAG_KEYWORDS + "");
            baseResult.setErrorCode(DcError.DC_OK);

            observer.onRequestSuccess(baseResult);
        }

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBMKeywords(result);
                    baseResult.setTag(Constants.NET_TAG_KEYWORDS + "");
                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    if(cache_province.equals("")){
                        observer.onRequestSuccess(baseResult);
                    }

                    sp.edit().putString("keywords",result);

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_KEYWORDS, mCurrentRequestId, 1001, "网络不给力，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;

    }

    /**
     * 获取城市列表
     *
     * @param observer
     * @return
     */
    public int requestForProvices(final Activity context, final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "getArea";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        final SharedPreferences sp = context.getSharedPreferences("cache", Context.MODE_PRIVATE);

        String cache_province = sp.getString("provinces", "");

        if (!cache_province.equals("")) {

            BaseResult baseResult = JSONParser.parseBMProvinceList(cache_province);
            baseResult.setTag(Constants.NET_TAG_GET_PROVINCE + "");

            baseResult.setErrorCode(DcError.DC_OK);

            observer.onRequestSuccess(baseResult);

        }else{

            new Runnable() {
                @Override
                public void run() {
                    try {


                        // 调用WebService
                        transport.call(soapAction, envelope);

                        // 获取返回的数据
                        SoapObject object = (SoapObject) envelope.bodyIn;
                        // 获取返回的结果
                        String result = object.getProperty(0).toString();

                        BaseResult baseResult = JSONParser.parseBMProvinceList(result);
                        baseResult.setTag(Constants.NET_TAG_GET_PROVINCE + "");

                        baseResult.setErrorCode(DcError.DC_OK);

                        Log.e("TAG", "webservice result " + result);

                        observer.onRequestSuccess(baseResult);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("provinces", result);
                        editor.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                        observer.onRequestError(Constants.NET_TAG_KEYWORDS, mCurrentRequestId, 1001, "网络不给力，请重试");
                    }
                }
            }.run();
        }


        return mCurrentRequestId;

    }

    /**
     * 获取类别列表
     *
     * @param observer
     * @return
     */
    public int getMarketTypeAndBrand(String keyword, String area, final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "getMarketTypeAndBrand";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);
        rpc.addProperty("keyword", keyword);
        rpc.addProperty("area", area);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();
        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBandAndModel(result);
                    baseResult.setTag(Constants.NET_TAG_GET_PROVINCE + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    observer.onRequestSuccess(baseResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_KEYWORDS, mCurrentRequestId, 1001, "网络不给力，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;

    }

    /**
     * 更改密码
     */
    public int requestChangePwd(String oldpwd, String newpwd, final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "alertPsw";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        rpc.addProperty("token", MineProfile.getInstance().getSessionID());
        rpc.addProperty("info", AES.getInstance().encrypt("{\"oldpsw\":\"" + oldpwd + "\",newpsw:\"" + newpwd + "\"}"));

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);


        mCurrentRequestId = transport.hashCode();
        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseChangePwd(result);
                    baseResult.setTag(Constants.NET_TAG_CHANGE_PWD + "");

                    if (baseResult.getSuccess() == 0) {
                        observer.onRequestSuccess(baseResult);
                    } else {
                        observer.onRequestError(Constants.NET_TAG_CHANGE_PWD, mCurrentRequestId, baseResult.getSuccess(), baseResult.getMessage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_CHANGE_PWD, mCurrentRequestId, 1001, "网络不给力，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * 根据关键字搜索游戏 tag = 242
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @param observer
     * @return
     */
    public int requestForSearch(String keyword, int area, String smalltype, String brand, int ismerge,int isCredit, int page, int pageSize, String sortField, int isAscSort, final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "getMarket";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        rpc.addProperty("token", MineProfile.getInstance().getSessionID());
        rpc.addProperty("keyword", keyword);
        rpc.addProperty("area", area);
        rpc.addProperty("bigtype", area);
        rpc.addProperty("smalltype", smalltype);
        rpc.addProperty("brand", brand);
        rpc.addProperty("isMerge", ismerge);
        rpc.addProperty("pageNo", page);
        rpc.addProperty("rows", pageSize);
        rpc.addProperty("sortField", sortField);
        rpc.addProperty("isAscSort", isAscSort);
        rpc.addProperty("isCredit", isCredit);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBMSearchProducts(result);
                    baseResult.setTag(Constants.NET_TAG_SEARCH + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    observer.onRequestSuccess(baseResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_SEARCH, mCurrentRequestId, 1001, "网络不给力，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * @param observer
     * @return
     * @author liushuohui
     */
    public int requestProductInfo(String supplyid, final IRequestListener observer) {


        // 调用的方法名称
        String methodName = "getProductAndCompanyInfo";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        rpc.addProperty("token", MineProfile.getInstance().getSessionID());
        rpc.addProperty("supplyId", supplyid);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBMProductInfo(result);
                    baseResult.setTag(Constants.NET_TAG_SEARCH + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                        observer.onRequestSuccess(baseResult);

                    Log.e("TAG", "webservice result " + result);


                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_SEARCH, mCurrentRequestId, 1001, "网络不给力，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * @param observer
     * @return
     */
    public int requestCollectProduct(int supplyid, int type, final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "addFavorites";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        rpc.addProperty("token", MineProfile.getInstance().getSessionID());
        rpc.addProperty("id", supplyid);
        rpc.addProperty("type", type);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parserBMUserLoginResult(result);
                    baseResult.setTag(Constants.NET_TAG_SEARCH + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    if (baseResult.getSuccess() == 1) {
                        observer.onRequestSuccess(baseResult);
                    } else {
                        observer.onRequestError(Constants.NET_TAG_CHANGE_PWD, mCurrentRequestId, baseResult.getSuccess(), baseResult.getMessage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    SoapFault sf = (SoapFault) envelope.bodyIn;
                    BaseResult base = JSONParser.parseBaseResult(sf.faultstring);
                    observer.onRequestError(Constants.NET_TAG_SEARCH, mCurrentRequestId, base.getSuccess(), base.getMessage());
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * @param observer
     * @return
     * @author liushuohui
     */
    public int requestGetCollection(int type, int page, final IRequestListener observer) {


        // 调用的方法名称
        String methodName = "findFavorites";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        rpc.addProperty("token", MineProfile.getInstance().getSessionID());
        rpc.addProperty("type", type);
        rpc.addProperty("pageNo", page);
        rpc.addProperty("row", 20);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBMCollectionResult(result);
                    baseResult.setTag(Constants.NET_TAG_SEARCH + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    observer.onRequestSuccess(baseResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_SEARCH, mCurrentRequestId, 9001, "网络不给力，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    public int requestGetCollectionCom(int type, int page, final IRequestListener observer) {


        // 调用的方法名称
        String methodName = "findFavorites";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        rpc.addProperty("token", MineProfile.getInstance().getSessionID());
        rpc.addProperty("type", type);
        rpc.addProperty("pageNo", page);
        rpc.addProperty("row", 20);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBMCollectionResult(result);
                    baseResult.setTag(Constants.NET_TAG_SEARCH + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    observer.onRequestSuccess(baseResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_SEARCH, mCurrentRequestId, 1001, "网络不给力，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * @param observer
     * @return
     * @author liushuohui
     */
    public int requestComDetail(final int userid, final IRequestListener observer) {


        // 调用的方法名称
        final String methodName = "getCompanyInfo";

        soapAction += methodName;
        new Runnable() {
            @Override
            public void run() {
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);

                rpc.addProperty("token", MineProfile.getInstance().getSessionID());
                rpc.addProperty("userid", userid);

                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

                envelope.bodyOut = rpc;
                // 设置是否调用的是dotNet开发的WebService
                envelope.dotNet = true;
                // 等价于envelope.bodyOut = rpc;
                envelope.setOutputSoapObject(rpc);

                final HttpTransportSE transport = new HttpTransportSE(endPoint);

                mCurrentRequestId = transport.hashCode();


                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parserBMComInfoResult(result);
                    baseResult.setTag(Constants.NET_TAG_SEARCH + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    observer.onRequestSuccess(baseResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_SEARCH, mCurrentRequestId, 1001, "网络不给力，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * 根据关键字搜索游戏 tag = 242
     *
     * @param observer
     * @return
     */
    public int requestForProductsPerCom(final int userid, final int page, final IRequestListener observer) {

        // 调用的方法名称
        final String methodName = "getProducts";

        soapAction += methodName;
        new Runnable() {
            @Override
            public void run() {
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);

                rpc.addProperty("token", MineProfile.getInstance().getSessionID());
                rpc.addProperty("userid", userid);
                rpc.addProperty("pageNo", page);
                rpc.addProperty("row", 20);


                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

                envelope.bodyOut = rpc;
                // 设置是否调用的是dotNet开发的WebService
                envelope.dotNet = true;
                // 等价于envelope.bodyOut = rpc;
                envelope.setOutputSoapObject(rpc);

                final HttpTransportSE transport = new HttpTransportSE(endPoint);

                mCurrentRequestId = transport.hashCode();


                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBMSearchProducts(result);
                    baseResult.setTag(Constants.NET_TAG_SEARCH + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    observer.onRequestSuccess(baseResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onRequestError(Constants.NET_TAG_SEARCH, mCurrentRequestId, 1001, "网络不给力，请重试");
                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * 根据关键字搜索游戏 tag = 242
     *
     * @param observer
     * @return
     */
    public int requestForUserinfo(final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "getMobileUserInfo";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        rpc.addProperty("token", MineProfile.getInstance().getSessionID());

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parseBMUserInfo(result);
                    baseResult.setTag(Constants.NET_TAG_USERINFO + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    if(baseResult.getSuccess() == 0){
                        observer.onRequestSuccess(baseResult);
                    }else{
                        observer.onRequestError(Constants.NET_TAG_MODIFYUSER,mCurrentRequestId,baseResult.getSuccess(),baseResult.getMessage());
                    }
//                    observer.onRequestSuccess(baseResult);

                } catch (Exception e) {
                    try{
                        SoapFault sf = (SoapFault) envelope.bodyIn;
                        observer.onRequestError(Constants.NET_TAG_USERINFO, mCurrentRequestId, 1001, sf.getMessage());
                    }catch(Exception ex){
//                        observer.onRequestError(Constants.NET_TAG_USERINFO,mCurrentRequestId,1001,"请重试！");
                    }

                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * 根据关键字搜索游戏 tag = 242
     *
     * @param observer
     * @return
     */
    public int updateUserinfo(final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "editUser";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        rpc.addProperty("token", MineProfile.getInstance().getSessionID());
        Log.e("TAG","upload user info : " + JSONBuilder.buildUpdateUserinfoString());
        rpc.addProperty("info", JSONBuilder.buildUpdateUserinfoString());

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parserBMUserLoginResult(result);
                    baseResult.setTag(Constants.NET_TAG_MODIFYUSER + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    if(baseResult.getSuccess() == 1){
                        observer.onRequestSuccess(baseResult);
                    }else{
                        observer.onRequestError(Constants.NET_TAG_MODIFYUSER,mCurrentRequestId,baseResult.getSuccess(),baseResult.getMessage());
                    }

                } catch (Exception e) {

                    SoapFault sf = (SoapFault) envelope.bodyIn;
                    observer.onRequestError(Constants.NET_TAG_MODIFYUSER, mCurrentRequestId, 1001, sf.getMessage());

                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * 上传头像
     *
     * @param observer
     * @return
     */
    public int uploadUserHead(String filename, String filepath, final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "upload";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        String base64 = ImgToBase64Util.imgToBase64(filepath);
        if (base64.equals("")) {
            observer.onRequestError(Constants.NET_TAG_UPLOAD_HEAD, -1, 1001, "头像获取失败");
            return -1;
        }
        rpc.addProperty("data", base64);
        rpc.addProperty("fileName", "123.png");
        rpc.addProperty("token", MineProfile.getInstance().getSessionID());

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        new Runnable() {
            @Override
            public void run() {
                try {
                    // 调用WebService
                    transport.call(soapAction, envelope);

                    // 获取返回的数据
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    // 获取返回的结果
                    String result = object.getProperty(0).toString();

                    BaseResult baseResult = JSONParser.parserBMUserLoginResult(result);
                    baseResult.setTag(Constants.NET_TAG_UPLOAD_HEAD + "");

                    baseResult.setErrorCode(DcError.DC_OK);

                    Log.e("TAG", "webservice result " + result);

                    observer.onRequestSuccess(baseResult);

                } catch (Exception e) {

                    SoapFault sf = (SoapFault) envelope.bodyIn;
                    observer.onRequestError(Constants.NET_TAG_UPLOAD_HEAD, mCurrentRequestId, 1001, sf.getMessage());

                }
            }
        }.run();


        return mCurrentRequestId;
    }

    /**
     * 获得城市列表
     *
     * @param observer
     * @return
     */
    public int requestCityList(Context context,final IRequestListener observer) {

        // 调用的方法名称
        String methodName = "getProvinceCity";

        soapAction += methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        rpc.addProperty("token", MineProfile.getInstance().getSessionID());

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        final HttpTransportSE transport = new HttpTransportSE(endPoint);

        mCurrentRequestId = transport.hashCode();

        final SharedPreferences sp = context.getSharedPreferences("cache", Context.MODE_PRIVATE);

        String cache_province = sp.getString("citys", "");

        if (!cache_province.equals("")) {

            BaseResult baseResult = JSONParser.parseCityList(cache_province);
            baseResult.setTag(Constants.NET_TAG_GET_PROVINCE + "");

            baseResult.setErrorCode(DcError.DC_OK);

            observer.onRequestSuccess(baseResult);

        }else{

            new Runnable() {
                @Override
                public void run() {
                    try {


                        // 调用WebService
                        transport.call(soapAction, envelope);

                        // 获取返回的数据
                        SoapObject object = (SoapObject) envelope.bodyIn;
                        // 获取返回的结果
                        String result = object.getProperty(0).toString();

                        BaseResult baseResult = JSONParser.parseCityList(result);
                        baseResult.setTag(Constants.NET_TAG_GET_PROVINCE + "");

                        baseResult.setErrorCode(DcError.DC_OK);

                        Log.e("TAG", "webservice result " + result);

                        observer.onRequestSuccess(baseResult);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("citys", result);
                        editor.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                        observer.onRequestError(Constants.NET_TAG_KEYWORDS, mCurrentRequestId, 1001, "网络不给力，请重试");
                    }
                }
            }.run();
        }

        return mCurrentRequestId;
    }
}
