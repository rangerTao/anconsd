package com.ranger.bmaterials.statistics;

import android.content.Context;

import com.baidu.mobstat.StatService;

public class ClickNumStatistics {

	// @author liushuohui | @data 2013-10-25 | 金币中心 充值卡点击统计
	public static void addCoinExchangeCardClickStatistics(Context context, int id) {
		switch (id) {
		case 1: // 移动
			StatService.onEvent(context, "cc_exch_card_cl", "金币中心移动充值卡兑换点击量");
			break;

		case 2: // 联通
			StatService.onEvent(context, "cc_exch_card_cl", "金币中心联通充值卡兑换点击量");
			break;

		case 3: // 电信
			StatService.onEvent(context, "cc_exch_card_cl", "金币中心电信充值卡兑换点击量");
			break;

		default:
			break;
		}
	}

	// @author liushuohui | @date 2013-09-24 | END <<

	public static void addMineTabClickNumStatistics(Context context) {
		StatService.onEvent(context, "mine_tab_click_num", "我的tab点击量");
	}

	public static void addMineGameClickNumStatistics(Context context) {
		StatService.onEvent(context, "mine_game_click_num", "游戏按钮点击量");
	}

	public static void addMineMsgClickNumStatistics(Context context) {
		StatService.onEvent(context, "mine_msg_click_num", "消息按钮点击量");
	}

	public static void addMineCollectionClickNumStatistics(Context context) {
		StatService.onEvent(context, "mine_collect_click", "收藏按钮点击量");
	}

	public static void addMineSwithUserClickNumStatistics(Context context) {
		StatService.onEvent(context, "swich_account_num", "编辑_切换帐号点击量");
	}

	public static void addMineEditBtnClickNumStatistics(Context context) {
		StatService.onEvent(context, "mine_editbtn_num", "编辑按钮点击量");
	}

	public static void addMenuClickNumStatistics(Context context) {
		StatService.onEvent(context, "menu_click_num", "menu tab点击量");
	}

	public static void addHomeStartStatistics(Context context, String gameName) {
		StatService.onEvent(context, "home_game_start", gameName);
	}

	public static void addHomeBannerStatistics(Context context) {
		StatService.onEvent(context, "home_banner", "总banner点击量");
	}

	public static void addHomeTabMyGamesStatistics(Context context) {
		StatService.onEvent(context, "home_tab_mygames", "'我的游戏'按钮点击量");
	}

	public static void addHomeTabCategoryStatistics(Context context) {
		StatService.onEvent(context, "home_tab_category", "'游戏分类'按钮点击量");
	}

	public static void addMyGamesStartStatistics(Context context, String gameName) {
		StatService.onEvent(context, "mygames_game_start", gameName);
	}

	public static void addMyGamesAddStatistics(Context context) {
		StatService.onEvent(context, "mygames_game_add", "我的游戏页'添加游戏'按钮点击量");
	}

	public static void addHallTabHomeStatistics(Context context) {
		StatService.onEvent(context, "hall_tab_home", "'首页'tab点击量");
	}

	public static void addHallTabGameStatistics(Context context) {
		StatService.onEvent(context, "hall_tab_game", "'游戏'tab点击量");
	}

	public static void addHallTabSearchStatistics(Context context) {
		StatService.onEvent(context, "hall_tab_search", "'搜索'tab点击量");
	}

	public static void addHallTabSquareStatistics(Context context) {
		StatService.onEvent(context, "hall_tab_square", "'发现'tab点击量");
	}

	public static void addSquareSnapNumStatistics(Context context) {
		StatService.onEvent(context, "square_snap_num", "发现页抢号点击次数");
	}
	public static void addSquareSnapNumDetailStatistics(Context context) {
		StatService.onEvent(context, "square_snap_detail_num", "发现页抢号详情点击次数");
	}
	public static void addSquareSnapNumDetailActionStatistics(Context context) {
		StatService.onEvent(context, "snap_action_num", "抢号详情点击抢号按钮次数");
	}

	public static void addSquareAppraisalStatistics(Context context) {
		StatService.onEvent(context, "square_appraisal_num", "发现页测评点击次数");
	}
	public static void addSquareAppraisalDetailStatistics(Context context) {
		StatService.onEvent(context, "square_appraisal_detail_num", "发现页测评详情点击次数");
	}
	public static void addSquareAppraisalDetailDownloadStatistics(Context context) {
		StatService.onEvent(context, "app_download_num", "测评详情点击下载次数");
	}

	public static void addSquareInformationStatistics(Context context) {
		StatService.onEvent(context, "square_information_num", "发现页资讯点击次数");
	}
	public static void addSquareInformationDetailStatistics(Context context) {
		StatService.onEvent(context, "square_information_detail_num", "发现页资讯详情点击次数");
	}
	public static void addSquareInformationDetailDownloadStatistics(Context context) {
		StatService.onEvent(context, "infos_download_num", "资讯详情点击下载次数");
	}

	public static void addSquareOpenServiceStatistics(Context context) {
		StatService.onEvent(context, "square_open_service_num", "发现页开服点击次数");
	}
	public static void addSquareOpenServiceDetailStatistics(Context context) {
		StatService.onEvent(context, "square_open_service_detail_num", "发现页开服详情点击次数");
	}
	public static void addSquareOpenServiceDetailDownloadStatistics(Context context) {
		StatService.onEvent(context, "open_service_download_num", "开服详情点击次数");
	}

	public static void addSquareActivityStatistics(Context context) {
		StatService.onEvent(context, "square_activity_num", "发现页活动点击次数");
	}
	public static void addSquareActivityDetailStatistics(Context context) {
		StatService.onEvent(context, "square_activity_detail_num", "发现页活动详情点击次数");
	}
	public static void addSquareActivityDetailDownloadStatistics(Context context) {
		StatService.onEvent(context, "act_download_num", "活动详情点击下载次数");
	}

	public static void addGameNewStatistics(Context context) {
		StatService.onEvent(context, "game_new", "游戏页最新点击次数");
	}

	public static void addGameRecommendMoreStatistics(Context context) {
		StatService.onEvent(context, "game_recommed_more", "游戏页推荐版块'更多游戏'按钮点击量");
	}

	public static void addGameBannerStatistics(Context context) {
		StatService.onEvent(context, "game_banner", "游戏推荐页banner点击量");
	}

	public static void addGameHotMoreStatistics(Context context) {
		StatService.onEvent(context, "game_hot_more", "游戏页排行版块'更多游戏'按钮点击量");
	}

	public static void addGameNewMoreStatistics(Context context) {
		StatService.onEvent(context, "game_new_more", "游戏页最新版块'更多游戏'按钮点击量");
	}

	public static void addGameClassItemStatistics(Context context, String id, String label) {
		if (null != context && id != null && null != label) {
			StatService.onEvent(context, "g_class_tab", "游戏分类页'" + label + "'点击量");
		}
	}

	public static void addGameTopicClickStatistics(Context context, String name) {
		if (null != context && null != name) {
			StatService.onEvent(context, "g_topic_tab", "游戏专题页'" + name + "'点击量");
		}
	}

	public static void addGameDetailSlideStatistics(Context context) {
		StatService.onEvent(context, "game_detail_slide", "详情页滑动次数");
	}

	public static void addGameGuideDetailOpenStatistics(Context context) {
		StatService.onEvent(context, "open_guide_detail", "攻略页PV");
	}

	public static void addMenuDownloadManagerStatistics(Context context) {
		StatService.onEvent(context, "click_download_manag", "管理按钮点击量");
	}

	public static void addHomeCoinClickStatistics(Context context) {
		StatService.onEvent(context, "home_coin_click", "首页-金币按钮点击量");
	}

	public static void addHomeMustPlayClickStatistics(Context context) {
		StatService.onEvent(context, "home_mustplay_click", "首页-必玩游戏按钮点击量");
	}

	public static void addHomeCompetitionClickStatistics(Context context) {
		StatService.onEvent(context, "home_competition_cli", "首页-争霸赛按钮点击量");
	}

	public static void addMineLoginClickStatistics(Context context) {
		StatService.onEvent(context, "mine_login_click", "我的-登录按钮点击量");
	}

	public static void addMineFastRegisterClickStatistics(Context context) {
		StatService.onEvent(context, "mine_fastregister_cl", "我的-一键注册按钮点击量");
	}

	public static void addLoginFastRegisterClickStatistics(Context context) {
		StatService.onEvent(context, "register_fast_click", "一键注册按钮点击量");
	}

	public static void addLoginButtonClickStatistics(Context context) {
		StatService.onEvent(context, "login_login_click", "登录按钮点击量");
	}

	public static void addCoinExchangeClickStatistics(Context context) {
		StatService.onEvent(context, "coin_exchangeClick", "兑换按钮点击量");
	}

	public static void addGameTabsClickStatistics(Context context, int position) {
		switch (position) {
		case 0: // 推荐
			StatService.onEvent(context, "game_recommend", "游戏页推荐点击次数");
			break;
		case 1: // 排行
			StatService.onEvent(context, "game_hot", "游戏页排行点击次数");
			break;
		case 2: // 分类
			StatService.onEvent(context, "game_classify", "游戏页分类点击次数");
			break;
		case 3: // 专题
			StatService.onEvent(context, "game_topic", "游戏页专题点击次数");
			break;
		default:
			break;
		}
	}

	public static void addCoinExchangeDetailStatistics(Context context) {
		StatService.onEvent(context, "coin_exdetail_click", "兑换详情按钮点击量");
	}

	public static void addMineCoinStatistis(Context context) {
		StatService.onEvent(context, "user_mineCoin_click", "我的-金币栏目点击量");
	}

	public static void addMineGameStatistis(Context context) {
		StatService.onEvent(context, "user_mineGame_click", "我的-游戏栏目点击量");
	}

	public static void addMineMessageStatistis(Context context) {
		StatService.onEvent(context, "user_mineMessage_click", "我的-消息栏目点击量");
	}

	public static void addMineCollectStatistis(Context context) {
		StatService.onEvent(context, "user_mineCollect_click", "我的-收藏栏目点击量");
	}

	public static void addCompetitionGameDlStatistis(Context context) {
		StatService.onEvent(context, "competition_gamedl_c", "争霸赛游戏总下载量");
	}

	public static void addCompetitionGameStartDlStatistis(Context context, String gamename) {
		StatService.onEvent(context, "compete_start_sum", gamename);
	}

	public static void addGetCoinRuleClickStatistis(Context context) {
		StatService.onEvent(context, "coin_getrule_click", "获取金币规则按钮点击量");
	}

	public static void addPushClickStatistis(Context cont, String notifyId) {
		StatService.onEvent(cont, "notify_from_push", notifyId);
	}

	public static void addPushReceivedStatistis(Context cont, String msg) {
		StatService.onEvent(cont, "push_notify_receive", msg);
	}

	public static void addEnterCoinCenterFromDetailStatistis(Context cont) {
		StatService.onEvent(cont, "detail_enter_ccenter", "从详情页进入金币中心");
	}

	public static void addGameDetailCommentsClickStatistis(Context cont, String gamename) {
		StatService.onEvent(cont, "detail_comment_click", gamename);
	}

	public static void addGameDetailRaidersClickStatistis(Context cont, String name) {
		StatService.onEvent(cont, "detail_raiders_click", name);
	}

	public static void addGameTabLatestClickStatistis(Context cont, String name) {
		StatService.onEvent(cont, "game_latest_click", name);
	}

	public static void addGameTabRankClickStatistis(Context cont, String name) {
		StatService.onEvent(cont, "game_rank_click", name);
	}

	public static void addGameTabRecommendedClickStatistis(Context cont, String name) {
		StatService.onEvent(cont, "game_recommend_click", name);
	}

	// 点击新品
	public static void addHomeTabNewGames(Context cx) {
		StatService.onEvent(cx, "home_tab_new_games", "点击首页新品");
	}

	// 点击分类
	public static void addHomeTabClassGames(Context cx) {
		StatService.onEvent(cx, "home_tab_class_games", "点击首页分类");
	}

	// 点击首页封面游戏
	public static void addHomeGameCoverStatistics(Context context, String gameName) {
		StatService.onEvent(context, "home_game_cover", gameName);
	}

	// 点击首页精品banner游戏
	public static void addHomeRecommendBannerGame(Context cx, String gameName) {
		StatService.onEvent(cx, "home_banner_game", gameName);
	}

	// 点击首页精品list游戏
	public static void addHomeRecommendListGame(Context cx, String gameName) {
		StatService.onEvent(cx, "home_recommend_list", gameName);
	}

	// 点击首页去别处逛逛
	public static void addHomeToOtherPage(Context cx) {
		StatService.onEvent(cx, "home_to_other_page", "点击首页去别处逛逛");
	}

	public static void addMenuSettingClickStatistis(Context cx) {
		StatService.onEvent(cx, "mine_menu_setting_cl", "设置按钮点击量");
	}

	public static void addMenuFeedBackClickStatistis(Context cx) {
		StatService.onEvent(cx, "mine_menu_feedback_c", "反馈按钮点击量");
	}

	public static void addMenuSharedClickStatistis(Context cx) {
		StatService.onEvent(cx, "mine_menu_share_clic", "分享按钮点击量");
	}

	public static void addMenuExitClickStatistis(Context cx) {
		StatService.onEvent(cx, "mine_menu_exit_click", "退出按钮点击量");
	}

	public static void addMenuCheckUpdateClickStatis(Context cx) {
		StatService.onEvent(cx, "mine_menu_checkupdat", "检查更新点击量");
	}

	public static void addSettingRecomdAppClickStatis(Context cx) {
		StatService.onEvent(cx, "setting_recommendapp", "设置-多酷书城推荐点击量");
	}

	public static void addSplashDetailClickStatis(Context cx) {
		StatService.onEvent(cx, "splash_detail_click", "启动页推送广告点击查看详情");
	}

	public static void addSnapNumberClickStatis(Context cx) {
		StatService.onEvent(cx, "snap_number_click", "抢号列表-抢号按钮点击量");
	}

	public static void addSearchButtonClickStatis(Context cx) {
		StatService.onEvent(cx, "search_button_click", "搜索按钮点击量");
	}

	public static void addKeywordChangeButtonClickStatis(Context cx) {
		StatService.onEvent(cx, "search_keyword_chang", "换一批搜索热词点击量");
	}

	public static void addManageDownloadClickStatis(Context cx) {
		StatService.onEvent(cx, "manager_download_cli", "管理-下载点击量");
	}

	public static void addManageUpdateClickStatis(Context cx) {
		StatService.onEvent(cx, "manager_update_click", "管理-更新点击量");
	}

	public static void addManageInstalledClickStatis(Context cx) {
		StatService.onEvent(cx, "manager_install_clic", "管理-已安装点击量");
	}

	public static void addManageStartAllClickStatis(Context cx) {
		StatService.onEvent(cx, "manager_start_all_cl", "管理下载-全部开始点击");
	}

	public static void addManageCancelAllClickStatis(Context cx) {
		StatService.onEvent(cx, "manager_cancel_all_c", "管理下载-全部取消点击");
	}

	public static void addManageUpdateAllClickStatis(Context cx) {
		StatService.onEvent(cx, "manager_update_all", "管理更新-全部更新点击");
	}

	public static void addManageIgnoreAllClickStatis(Context cx) {
		StatService.onEvent(cx, "manager_ignore_all", "管理更新-查看忽略点击");
	}

	public static void addSearchResultItemClickStatis(Context cx, String position) {
		StatService.onEvent(cx, "search_result_item", position);
	}

	public static void addSearchResultItemButtonClickStatis(Context cx, String position) {
		StatService.onEvent(cx, "search_result_button", position);
	}

	public static void addRecDiaDetailButtonClickStatis(Context cx, String gameName) {
		StatService.onEvent(cx, "rec_dg_game_detail", gameName);
	}

	public static void addRecDiaInfoDetailButtonClickStatis(Context cx, String infoId) {
		StatService.onEvent(cx, "rec_dg_info_detail", infoId);
	}
	
	public static void addGameRelatedInfoClickStatis(Context cx) {
		StatService.onEvent(cx, "click_game_related", "游戏相关信息展开");
	}
	
	public static void addLoginBdPassportClickStatis(Context cx) {
		StatService.onEvent(cx, "click_login_bdpassport", "百度passport登录");
	}
	
	public static void addJump2RelatedInfoClickStatis(Context cx, String infoId) {
		StatService.onEvent(cx, "click_in_game_relatedInfo", "从游戏相关信息进资讯详情"+infoId);
	}

    public static void addGameRecomClickStatis(Context cx,String gameid){
        StatService.onEvent(cx, "click_recom_popup", gameid);
    }

    public static void addGameRecomClickStatisDetail(Context cx,String gameid){
        StatService.onEvent(cx, "click_recom_detail", gameid);
    }
}
