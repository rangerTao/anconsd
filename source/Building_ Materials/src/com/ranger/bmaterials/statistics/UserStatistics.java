package com.ranger.bmaterials.statistics;

import android.content.Context;

import com.baidu.mobstat.StatService;

public class UserStatistics {
	
	public static void addRegisterNumStatistics(Context context)
	{
		StatService.onEvent(context, "user_register", "用户注册");
	}
	
	public static void addLoginNumStatistics(Context context)
	{
		StatService.onEvent(context, "user_login", "用户登陆");
	}
	
	public static void addFastRegSuccessStatistics(Context context)
	{
		StatService.onEvent(context, "user_fast_registersuccess", "一键注册成功用户数");
	}
	
	public static void addManualRegSuccessStatistics(Context context)
	{
		StatService.onEvent(context, "user_manual_registersuccess", "手动注册成功用户数");
	}
}
