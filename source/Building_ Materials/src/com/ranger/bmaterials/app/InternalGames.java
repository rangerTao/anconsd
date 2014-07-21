package com.ranger.bmaterials.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class InternalGames {
	private InternalGames() {
	}

	public static class InternalInstalledGames {
		private InternalInstalledGames() {
		}

		private static final String installed_pkgname_sp = "installed_pkgname";

		// 添加应用内安装的游戏
		public static void addInternalInstalledGames(Context cx, String pkgName) {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(cx);
			String pkgnames_value = sp.getString(installed_pkgname_sp, null);
			if (pkgnames_value == null)
				pkgnames_value = pkgName;
			else {
				List<String> pkgnames = Arrays.asList(pkgnames_value
						.split("\\|"));
				if (!pkgnames.contains(pkgName))
					pkgnames_value += "|" + pkgName;
			}
			sp.edit().putString(installed_pkgname_sp, pkgnames_value).commit();
		}

		/**
		 * 获取应用内安装的游戏
		 * 
		 * @param cx
		 * @return pkgname list
		 */
		public static ArrayList<String> getInternalInstalledGames(Context cx) {
			ArrayList<String> list = new ArrayList<String>();
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(cx);
			String pkgnames_value = sp.getString(installed_pkgname_sp, null);
			if (pkgnames_value != null) {
				String[] pkgnames = pkgnames_value.split("\\|");
				for (String pkgname : pkgnames) {
					if (!pkgname.equals("") && !list.contains(pkgname))
						list.add(pkgname);
				}
			}
			return list;
		}

		/**
		 * 卸载后删除记录
		 */
		public static void deleteInternalInstalledGames(Context cx,
				String pkgName) {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(cx);
			String pkgnames_value = sp.getString(installed_pkgname_sp, null);
			if (pkgnames_value != null) {
				String[] pkgnames = pkgnames_value.split("\\|");
				String new_pkgnames_value = null;
				for (String pkgname : pkgnames) {
					if (!pkgname.equals("") && !pkgname.equals(pkgName)) {
						if (new_pkgnames_value == null)
							new_pkgnames_value = pkgname;
						else
							new_pkgnames_value += "|" + pkgname;
					}
				}
				sp.edit().putString(installed_pkgname_sp, new_pkgnames_value)
						.commit();
			}
		}
	}

	public static class InternalStartGames {
		private InternalStartGames() {
		}

		private static final String START_GAME_STATISTICS_SP = "start_game_statistics";

		public static void addStartGame(Context cx, String pkgName) {
			SharedPreferences started_app_sp = cx.getSharedPreferences(
					START_GAME_STATISTICS_SP, Context.MODE_PRIVATE);
			started_app_sp.edit().putLong(pkgName, System.currentTimeMillis())
					.commit();
		}

		public static SharedPreferences getSharedPreferences(Context cx) {
			return cx.getSharedPreferences(START_GAME_STATISTICS_SP,
					Context.MODE_PRIVATE);

		}

		public static void deleteStartGame(Context cx, String pkgName) {
			SharedPreferences started_app_sp = cx.getSharedPreferences(
					START_GAME_STATISTICS_SP, Context.MODE_PRIVATE);
			started_app_sp.edit().putLong(pkgName, 0).commit();

		}
	}
}
