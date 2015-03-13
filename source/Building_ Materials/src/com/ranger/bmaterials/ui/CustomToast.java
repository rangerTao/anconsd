package com.ranger.bmaterials.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.tools.MyLogger;

public class CustomToast {

	// toast提示码
	public static final int DC_ERR_NO_MORE_DATA = 10001; // 没有更多数据
	public static final int DC_ERR_NICKNAME_NOT_CHANGED = 10002; // 昵称未修改
	public static final int DC_ERR_OLD_PWD_EMPTY = 10003;// 旧密码不能为空
	public static final int DC_ERR_NEW_PWD_EMPTY = 10004;// 新密码不能为空
	public static final int DC_ERR_INVALID_PWD = 10005;// 密码格式有误（6-16位字母或数字）
	public static final int DC_ERR_INVALID_FEEDBACK_CONTENT = 10006;// 请输入有效内容
	public static final int DC_ERR_INVALID_CONTACT = 10007;// 请输入正确的手机号或邮箱地址
	public static final int DC_ERR_INVALID_USERNAME_OR_PHONENUM = 10008;// 请输入正确的用户名或手机号
	public static final int DC_ERR_INVALID_USERNAME = 10009;// 请输入正确的用户名(4-14位字母或数字)
	public static final int DC_ERR_INVALID_USERNAME_OR_PWD = 10010;// 用户名或密码错误，请重新输入
	public static final int DC_ERR_LOGIN_EXPIRED = 10011;// 登录失败：密码已过期，请重新输入密码
	public static final int DC_ERR_GET_ROOT_FAILED = 10012;// 获取root权限失败
	public static final int DC_ERR_EXIST_PHONENUM = 10013;// 此号码已被使用，请直接进行登陆
	public static final int DC_ERR_NICKNAME_IS_INUSE = 10014;// 昵称已经被使用
	public static final int DC_ERR_NICKNAME_BAD_FORMAT = 10015;// 昵称不符合规则
	public static final int DC_ERR_GET_VERIFYCODE_FAILED = 10016;// 获取验证码失败
	public static final int DC_ERR_USERNAME_CANNOT_BE_PHONENUM = 10017;// 用户名不能为手机号
	public static final int DC_ERR_NEW_PWD_CANNOT_BE_OLD_PWD = 10018;// 新旧密码不能一致
	public static final int DC_ERR_WRONG_USERNAME = 10019;// 用户名不能为1开头的11位数字，请重新输入
	public static final int DC_ERR_INVALID_OLDPWD = 10020;// 旧密码格式有误（6-16位字母或数字）

	public static final int DC_OK_CHANGED_NICKNAME = 20001; // 昵称修改成功
	public static final int DC_OK_BIND_PHONENUM = 20002; // 绑定手机号成功
	public static final int DC_OK_CHNAGE_PWD = 20003; // 修改密码成功
	public static final int DC_OK_FEEDBACK = 20004; // 反馈成功
	public static final int DC_OK_CLEAR_PIC_CACHE = 20005; // 清除图片缓存成功
	public static final int DC_OK_REMOVE_PKG = 20006; // 删除安装包成功
	public static final int DC_OK_CHECK_VERSION = 20007; // 检查更新，最新版本
	public static final int DC_Err_NEED_REGISTER_MANUALLY = 20008; // 手动注册
	
//	private static Handler handler;;
//	private static TimerTask timertask;;
//	private static Timer timer;

	public static void showToast(Context context, String msg) {

        try{
            View toastRoot = null;
            TextView message = null;

            if (toastStart2 == null) {
                toastStart2 = new Toast(context);
                toastStart2.setDuration(Toast.LENGTH_SHORT);
                toastRoot = View.inflate(context, R.layout.toast_login_register_error, null);
                toastStart2.setView(toastRoot);
            } else {
                toastRoot = toastStart2.getView();
            }
            if(toastRoot != null){
                message = (TextView) toastRoot.findViewById(R.id.label_error_message);
                message.setText(msg);
                toastStart2.show();
            }
        }catch (Exception e){

        }

	}

	private static Toast toastStart2;

	public static void showLoginRegistSuccessToast(Context context, int errorCode) {
		switch (errorCode) {
		case DC_OK_CHANGED_NICKNAME:
			showToast(context, context.getResources().getString(R.string.change_nickname_succeed_tip));
			break;
		case DC_OK_BIND_PHONENUM:
			showToast(context, context.getResources().getString(R.string.bind_phone_succeed_tip));
			break;
		case DC_OK_CHNAGE_PWD:
			showToast(context, context.getResources().getString(R.string.change_pwd_succeed_tip));
			break;
		case DC_OK_FEEDBACK:
			showToast(context, context.getResources().getString(R.string.feedback_succeed_tip));
			break;
		case DC_OK_CLEAR_PIC_CACHE:
			showToast(context, context.getResources().getString(R.string.clear_pic_cache_tip));
			break;
		case DC_OK_REMOVE_PKG:
			showToast(context, context.getResources().getString(R.string.delete_pkg_tip));
			break;
		case DC_OK_CHECK_VERSION:
			showToast(context, context.getResources().getString(R.string.latest_version_tip));
			break;
		default:
			break;
		}
	}


//	private static void startTimer() {
//		timer = new Timer(false);
//		handler = new Handler() {
//			public void handleMessage(Message msg) {
//				endTimer();
//				super.handleMessage(msg);
//			}
//		};
//
//		timertask = new TimerTask() {
//			public void run() {
//				Message message = new Message();
//				message.what = 1;
//				handler.sendMessage(message);
//			}
//		};
//
//		timer.schedule(timertask, 2000, 2000);
//	}
//
//	private static void endTimer() {
//		if (timer != null) {
//			timer.cancel();
//			timer = null;
//		}
//	}

	public static void showLoginRegistErrorToast(Context context, int errorCode) {

		MyLogger.getLogger("LoginRegisterToast").d("errorCode: " + errorCode);

		switch (errorCode) {

		case DcError.DC_NET_TIME_OUT:
		case DcError.DC_NET_GENER_ERROR:
			CustomToast.showToast(context, context.getResources().getString(R.string.network_error_tip));
			break;

		case DcError.DC_NET_DATA_ERROR:
			CustomToast.showToast(context, context.getResources().getString(R.string.request_failed_tip));
			break;

		case DcError.DC_JSON_PARSER_ERROR:
			showToast(context, context.getResources().getString(R.string.json_parser_error_tip));
			break;

		case DcError.DC_NEEDLOGIN:
			showToast(context, context.getResources().getString(R.string.need_login_tip));
			break;

		case DcError.DC_HAVE_SENSITIVE_WORD:
			CustomToast.showToast(context, context.getResources().getString(R.string.sensitive_error_tip));
			break;

		case DcError.DC_ONCE_IN_SEVEN_DAYS:
			CustomToast.showToast(context, context.getResources().getString(R.string.once_in_seven_days_tip));
			break;

		case DcError.DC_EXIST_NICKNAME:
			CustomToast.showToast(context, context.getResources().getString(R.string.exist_nickname_tip));
			break;

		case DC_ERR_NICKNAME_NOT_CHANGED:
			CustomToast.showToast(context, context.getResources().getString(R.string.nickname_not_change_tip));
			break;

		case DcError.DC_VERIFYCODE_ERROR:
			CustomToast.showToast(context, context.getResources().getString(R.string.verifycode_error_tip));
			break;

		case DcError.DC_VERIFYCODE_EXPIREED:
			CustomToast.showToast(context, context.getResources().getString(R.string.valid_verifycode_tip));
			break;

		case DcError.DC_HAVE_BIND:
			CustomToast.showToast(context, context.getResources().getString(R.string.have_bind_tip));
			break;
		case DcError.DC_BADPWD:
			CustomToast.showToast(context, context.getResources().getString(R.string.badpwd_tip));
			break;

		case DC_ERR_OLD_PWD_EMPTY:
			CustomToast.showToast(context, context.getResources().getString(R.string.old_pwd_empty_tip));
			break;
		case DC_ERR_NEW_PWD_EMPTY:
			CustomToast.showToast(context, context.getResources().getString(R.string.new_pwd_empty_tip));
			break;
		case DC_ERR_INVALID_PWD:
			CustomToast.showToast(context, context.getResources().getString(R.string.invalid_pwd_tip));
			break;
		case DC_ERR_INVALID_OLDPWD:
			CustomToast.showToast(context, context.getResources().getString(R.string.invalid_oldpwd_tip));
			break;

		case DC_ERR_INVALID_FEEDBACK_CONTENT:
			CustomToast.showToast(context, context.getResources().getString(R.string.invalid_feedback_content_tip));
			break;
		case DC_ERR_INVALID_CONTACT:
			CustomToast.showToast(context, context.getResources().getString(R.string.invalid_contact_tip));
			break;
		case DC_ERR_INVALID_USERNAME_OR_PHONENUM:
			CustomToast.showToast(context, context.getResources().getString(R.string.invalide_username_phonenum_tip));
			break;
		case DcError.DC_USER_NOT_EXIST:
			CustomToast.showToast(context, context.getResources().getString(R.string.user_not_exist_tip));
			break;
		case DC_ERR_INVALID_USERNAME:
			CustomToast.showToast(context, context.getResources().getString(R.string.invalid_username_tip));
			break;
		case DcError.DC_USERNAME_INVALID:
		case DC_ERR_WRONG_USERNAME:
			CustomToast.showToast(context, context.getResources().getString(R.string.invalid_username_tip2));
			break;
			
		case DC_ERR_INVALID_USERNAME_OR_PWD:
			CustomToast.showToast(context, context.getResources().getString(R.string.invalide_username_pwd_tip));
			break;
		case DC_ERR_LOGIN_EXPIRED:
			CustomToast.showToast(context, context.getResources().getString(R.string.login_expired_tip));
			break;
		case DC_ERR_GET_ROOT_FAILED:
			CustomToast.showToast(context, context.getResources().getString(R.string.get_root_failed_tip));
			break;
		case DcError.DC_EXIST_USER:
			CustomToast.showToast(context, context.getResources().getString(R.string.exist_username_tip));
			break;
		case DC_ERR_EXIST_PHONENUM:
			CustomToast.showToast(context, context.getResources().getString(R.string.exist_phonenum_tip));
			break;
		case DC_ERR_NICKNAME_IS_INUSE:
			CustomToast.showToast(context, context.getResources().getString(R.string.nickname_inuse_tip));
			break;
		case DC_ERR_NICKNAME_BAD_FORMAT:
			CustomToast.showToast(context, context.getResources().getString(R.string.nickname_bad_format_tip));
			break;
		case DC_ERR_GET_VERIFYCODE_FAILED:
			CustomToast.showToast(context, context.getResources().getString(R.string.get_verifycode_failed_tip));
			break;
		case DC_ERR_USERNAME_CANNOT_BE_PHONENUM:
			CustomToast.showToast(context, context.getResources().getString(R.string.username_cannot_be_phonenum_tip));
			break;
		case DC_ERR_NO_MORE_DATA:
			CustomToast.showToast(context, context.getResources().getString(R.string.no_more_data_tip));
			break;
		case DC_ERR_NEW_PWD_CANNOT_BE_OLD_PWD:
			CustomToast.showToast(context, context.getResources().getString(R.string.new_pwd_cannot_be_old_pwd_tip));
			break;
		case DC_Err_NEED_REGISTER_MANUALLY:
			CustomToast.showToast(context, context.getResources().getString(R.string.fast_register_failed));
			break;
		default:
//			CustomToast.showToast(context, context.getResources().getString(R.string.request_failed_tip));
			break;
		}
	}
}
