package com.ranger.lpa.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.lpa.R;

public class CustomToast {

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
}
