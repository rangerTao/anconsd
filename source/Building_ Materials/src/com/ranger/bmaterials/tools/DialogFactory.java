package com.ranger.bmaterials.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.BMApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.listener.onEditUserInfoDialogDismissListener;
import com.ranger.bmaterials.ui.BMUserinfoActivity;
import com.ranger.bmaterials.ui.CustomProgressDialog;

public class DialogFactory {

    private static Context mContext;
    private static CustomProgressDialog progressDialog;
    private static Handler mHandler;
    private static boolean rootResult = false;

    private static EditText etContent;

    public static Dialog createCheckRootDownDialog(Context context, final int type, String title, final onEditUserInfoDialogDismissListener listener) {
        mContext = context;
        final Dialog dialog = new Dialog(context, R.style.dialog);

        LayoutInflater factory = LayoutInflater.from(context);
        View dialogView = factory.inflate(R.layout.dialog_edit_userinfo, null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.label_edit_userinfo);
        tvTitle.setText(title);

        etContent = (EditText) dialogView.findViewById(R.id.et_userinfo_content);

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int btnId = v.getId();
                switch (btnId) {
                    case R.id.btn_cancel:
                        dialog.dismiss();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        break;
                    case R.id.btn_commit:

                        String content = etContent.getText().toString();

                        switch (type) {
                            case BMUserinfoActivity.USERINFO_EDIT_TYPE_NICK:
                                MineProfile.getInstance().setNickName(content);
                                break;
                            case BMUserinfoActivity.USERINFO_EDIT_TYPE_NAME:
                                MineProfile.getInstance().setUserName(content);
                                break;
                            case BMUserinfoActivity.USERINFO_EDIT_TYPE_SEX:
                                MineProfile.getInstance().setUserType(content.equals("男") ? 1 : 0);
                                break;
                            case BMUserinfoActivity.USERINFO_EDIT_TYPE_AREA:
                                MineProfile.getInstance().setArea(content);
                                break;
                            case BMUserinfoActivity.USERINFO_EDIT_TYPE_SIGN:
                                MineProfile.getInstance().setSignture(content);
                                break;
                        }

                        MineProfile.getInstance().Save();

                        dialog.dismiss();

                        listener.onEditUserInfoDialogDismissed(etContent.getText().toString());

                        break;
                    default:
                        break;
                }
            }
        };

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(clickListener);
        dialogView.findViewById(R.id.btn_commit).setOnClickListener(clickListener);

        DisplayMetrics dm = BMApplication.getAppInstance().getResources().getDisplayMetrics();
        int width = dm.widthPixels - PhoneHelper.dip2px(mContext, 13) * 2;

        dialog.addContentView(dialogView, new LayoutParams(width, LayoutParams.WRAP_CONTENT));
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });

        return dialog;
    }
}
