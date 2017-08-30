package com.penghaonan.homemonitor.server;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.appframework.utils.CommonUtils;
import com.penghaonan.homemonitor.server.base.BaseActivity;
import com.penghaonan.homemonitor.server.manager.CommandManager;
import com.penghaonan.homemonitor.server.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.local.LocalMessengerAdapter;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity implements LocalMessengerAdapter.LocalMessageListener, AMessengerAdapter.MessengerStateListener {

    @BindView(R.id.cmd_input)
    EditText mCmdInputView;

    @BindView(R.id.tv_output)
    TextView mOutputView;

    @BindView(R.id.qrcode_view)
    ImageView mQRCodeView;

    @BindView(R.id.tv_server_id)
    TextView mServerIdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        createQRCode();
        startService(new Intent(this, MainService.class));
        CommandManager.getInstance().getMessengerAdapter().addMessengerStateListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommandManager.getInstance().getMessengerAdapter().removeMessengerStateListener(this);
    }

    private void createQRCode() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                final String sid = CommandManager.getInstance().getMessengerAdapter().getServerId();
                DisplayMetrics metric = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metric);
                int width = metric.widthPixels;
                final Bitmap bitmap = encodeAsBitmap(sid, width / 2);
                if (bitmap != null) {
                    AppDelegate.post(new Runnable() {
                        @Override
                        public void run() {
                            mQRCodeView.setImageBitmap(bitmap);
                            mServerIdView.setText(sid);
                            checkPermissions();
                        }
                    });
                }
            }
        }.start();
    }

    private Bitmap encodeAsBitmap(String str, int size) {
        Bitmap bitmap = null;
        BitMatrix result;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, size, size);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException iae) {
            return null;
        }

        return bitmap;
    }

    private void checkPermissions() {
        List<String> permissons = new LinkedList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissons.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        permissons.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissons.add(Manifest.permission.CAMERA);
        permissons.add(Manifest.permission.RECORD_AUDIO);
        CommonUtils.checkPermission(this, permissons);
    }

    @Override
    public void onMessageReceived(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOutputView.append(msg);
                mOutputView.append("\n");
            }
        });
    }

    @Override
    public void onMessengerEnable(boolean enable) {
        if (enable) {
            createQRCode();
        } else {
            mQRCodeView.setImageDrawable(null);
            mServerIdView.setText("");
        }
    }
}
