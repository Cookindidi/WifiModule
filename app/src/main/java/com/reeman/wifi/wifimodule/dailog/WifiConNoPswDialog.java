package com.reeman.wifi.wifimodule.dailog;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.reeman.wifi.wifimodule.R;
import com.reeman.wifi.wifimodule.utils.WifiUtils;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by reeman on 2017/5/9.
 */

public class WifiConNoPswDialog {

    Context context;
    Dialog mDialog;
    Handler mHandler;

    public WifiConNoPswDialog(final Context context,Handler handler) {
        this.context = context;
        mHandler = handler;
        mDialog = new Dialog(context, R.style.MyDialog);
        final View layout = View.inflate(context, R.layout.dialog_conn_no_password, null);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(layout, new ViewGroup.LayoutParams(600, 400));
        initView(layout);
        initData();
    }

    TextView txtWifiName;
    Button BtnCancel, BtnConn;

    private void initView(View layout) {
        txtWifiName = (TextView) layout.findViewById(R.id.txt_wifi_name);
        BtnCancel = (Button) layout.findViewById(R.id.btn_cancel);
        BtnConn = (Button) layout.findViewById(R.id.btn_connect);
    }


    private void checkConnected(final boolean isConnected){
        Timer checkConnectedTimer = new Timer();
        checkConnectedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isConnected){
                    mHandler.sendEmptyMessage(WifiUtils.LINE_WIFI_ERROR);
                }
            }
        },1500);
    }

    private void initData() {
        BtnConn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isConnected = WifiUtils.getInstance(context).connectNoPassWordWifi(scanResult);
                checkConnected(isConnected);
                dissmiss();
            }
        });
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dissmiss();
            }
        });
    }


    ScanResult scanResult;

    public void show(ScanResult scanResult) {
        this.scanResult = scanResult;
        txtWifiName.setText("WiFiName : " + scanResult.SSID);
        mDialog.show();
    }

    public void dissmiss() {
        if (mDialog == null) {
            return;
        }
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

}
