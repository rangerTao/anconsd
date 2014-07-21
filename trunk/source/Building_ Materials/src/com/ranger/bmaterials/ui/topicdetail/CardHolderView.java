package com.ranger.bmaterials.ui.topicdetail;

import android.widget.RatingBar;
import android.widget.TextView;

import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.CircleProgressBar;
import com.ranger.bmaterials.view.GameLabelView;
import com.ranger.bmaterials.view.ImageViewForList;

public class CardHolderView {
	public TextView card_name, card_download_times, card_size, card_download_tv,
			card_recommend_tv, card_pb_tv;
	public GameLabelView card_game_label;
	public RoundCornerImageView card_icon;
	public CircleProgressBar card_download_iv;
	public RatingBar card_rating;
}
