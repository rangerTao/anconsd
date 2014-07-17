package com.ranger.lpa.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.ranger.lpa.R;
import com.ranger.lpa.camera.CameraManager;
import com.ranger.lpa.decoding.CaptureActivityHandler;
import com.ranger.lpa.decoding.InactivityTimer;
import com.ranger.lpa.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by taoliang on 14-7-16.
 */
public class BarcodeScannerActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback {

    public static int RESULT_BARCODE = 0x1;

    private CaptureActivityHandler handler;
    private ViewfinderView mfindView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private boolean vibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_barcode_scan);

        CameraManager.init(this);

        mfindView = (ViewfinderView) findViewById(R.id.view_barcode_finder);

        findViewById(R.id.btn_cancel_scan).setOnClickListener(this);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        String resultString = result.getText();
        if (resultString.equals("")) {
        }else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("result", resultString);
            this.setResult(RESULT_BARCODE, resultIntent);
            finish();
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    public com.ranger.lpa.view.ViewfinderView getViewfinderView() {
        return mfindView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        mfindView.drawViewfinder();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_cancel_scan:
                setResult(0);
                finish();
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }
}
