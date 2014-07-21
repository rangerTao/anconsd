package com.ranger.bmaterials.sapi.util;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ProgressBar;

import com.baidu.sapi2.SapiWebView;
import com.baidu.sapi2.utils.L;
import com.ranger.bmaterials.R;

/**
 * {@link com.baidu.sapi2.SapiWebView}相关工具类。
 *
 * @author zhoukeke
 * @version 6.2.4
 * @since 6.2.4
 */
public class SapiWebViewUtil {

    /**
     * Shortcut method
     *
     * @param context {@link android.content.Context}
     * @param sapiWebView {@link com.baidu.sapi2.SapiWebView}
     */
    public static void addCustomView(final Context context, final SapiWebView sapiWebView) {
        setProgressBar(context, sapiWebView);
        setNoNetworkView(context, sapiWebView);
        setTimeoutView(context, sapiWebView);
    }

    /**
     * 设置无网络情况下的view
     *
     * @param context {@link android.content.Context}
     * @param sapiWebView {@link com.baidu.sapi2.SapiWebView}
     */
    public static void setNoNetworkView(final Context context, final SapiWebView sapiWebView){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View noNetworkView = inflater.inflate(R.layout.layout_sapi_network_unavailable, null);
        View.OnClickListener openSettings = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(intent);
            }
        };
        //打开网络设置按钮，默认隐藏，如需要可以显示
        noNetworkView.findViewById(R.id.btn_network_settings).setOnClickListener(openSettings);

        sapiWebView.setNoNetworkView(noNetworkView);
    }

    /**
     * 设置加载页面超时的view
     *
     * @param context {@link android.content.Context}
     * @param sapiWebView {@link com.baidu.sapi2.SapiWebView}
     */
    public static void setTimeoutView(final Context context, final SapiWebView sapiWebView){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View timeoutView = inflater.inflate(R.layout.layout_sapi_loading_timeout, null);
        View btnRetry = timeoutView.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                sapiWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        timeoutView.setVisibility(View.INVISIBLE);
                        sapiWebView.reload();
                    }
                });
            }
        });

        sapiWebView.setTimeoutView(timeoutView);
    }

    /**
     * 设置自定义{@link android.widget.ProgressBar}
     *
     * @param context {@link android.content.Context}
     * @param sapiWebView {@link com.baidu.sapi2.SapiWebView}
     */
    @SuppressWarnings("deprecation")
    public static void  setProgressBar(final Context context, final SapiWebView sapiWebView) {
        try {
            ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setLayoutParams(new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 4, 0, 0));

            sapiWebView.setProgressBar(progressBar);
        } catch (Throwable e) {
            // 厂商bug容错：java.lang.RuntimeException: Unable to start activity ComponentInfo{packageName/activityName}: java.lang.RuntimeException: : You must supply a layout_height attribute.
            L.e(e);
        }
    }
}
