package com.ranger.bmaterials.app;

import com.ranger.bmaterials.ui.CustomToast;

public final class DcError {
	
	public static final int DC_Error									= -1;			// 错误
	public static final int DC_OK 									= 0; 			// 正常
	
	// 网络错误码定义
	public static final int DC_NET_TIME_OUT					= 504;		// 网络超时
	public static final int DC_NET_DATA_ERROR				= 1000;		// 网络传输数据错误
	public static final int DC_NET_GENER_ERROR 			= 1001;		// 网络通用错误标识
	
	// 逻辑错误码定义
	public static final int DC_JSON_PARSER_ERROR		= 1002;		// JSON解析错误
	public static final int DC_BADPWD 							= 1003;		// 密码错误
	public static final int DC_NEEDLOGIN 						= 1004;		// 需要登录
	public static final int DC_BADPARAM 						= 1005;		// 参数错误
	public static final int DC_BADACCOUNT 					= 1006;		// 无效用户
	public static final int DC_EXIST_USER						= 1010;		// 用户已存在
	public static final int DC_USER_NOT_EXIST				= 1011;		// 用户不存在
	public static final int DC_HAVE_BIND						= 1012;		// 已绑定
	public static final int DC_VERIFYCODE_ERROR			= 1013;		// 验证码出错 
	public static final int DC_VERIFYCODE_EXPIREED	    = 1016;		// 验证码过期 
	public static final int DC_EXIST_NICKNAME				= 1014;     // 昵称已存在
	public static final int DC_NICKNAME_INVALID			= 1015;     //昵称不符合规则
	public static final int DC_USERNAME_INVALID			= 1022;     //用户名不符合规则 
	public static final int DC_GAME_OUTOFSTOCK			= 1023;     //游戏已下架
	public static final int DC_HAVE_SENSITIVE_WORD		= 1024;     //敏感词错误
	public static final int DC_NO_GAME_NUMBER			= 1025;     //抢号号码被抢光
	public static final int DC_REACH_MAX_COLLECTION_NUM			= 1026;     //最多能收藏2000
	public static final int DC_ONCE_IN_SEVEN_DAYS			= 1027;     //7天内只可修改一次昵称
	public static final int DC_GAME_NOT_FOUND               = 1028;     //暂无此游戏
	public static final int DC_NEED_NICK_NAME               = 1029;     //账号无昵称
	/**
	 * 今天已兑换过
	 */
	public static final int DC_COIN_EXCHANGED_OUT           = 1031;
    /**
     * 支付异常
     */
    public static final int DC_PAY_EXCEPTION                = 1034;
    /**
     * 余额不足
     */
    public static final int DC_NO_MONEY                     = 1035;
    /**
     * 库存数量不足
     */
    public static final int DC_NO_MORE_CARD                 = 1036;
}

