package com.reeman.wifi.wifimodule.dailog;

import android.app.Dialog;
import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.reeman.wifi.wifimodule.R;
import com.reeman.wifi.wifimodule.utils.WifiUtils;


/**
 * Created by reeman on 2017/5/9.
 */

public class WifiHasLinkedDialog implements View.OnClickListener{

    Context context;
    Dialog mDialog;

    public WifiHasLinkedDialog(final Context context) {
        this.context = context;
        mDialog = new Dialog(context, R.style.MyDialog);
        final View layout = View.inflate(context, R.layout.dialog_state_alert, null);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(layout, new ViewGroup.LayoutParams(800, 300));
        initView(layout);
    }

    TextView txtWifiName, tv_wifi_safe, tv_wifi_level, tv_wifi_ip;
    Button BtnCancel, btn_forgot;

    private void initView(View layout) {
        tv_wifi_ip = (TextView) layout.findViewById(R.id.tv_wifi_ip);
        tv_wifi_safe = (TextView) layout.findViewById(R.id.tv_wifi_safe);
        tv_wifi_level = (TextView) layout.findViewById(R.id.tv_wifi_level);
        txtWifiName = (TextView) layout.findViewById(R.id.txt_wifi_name);
        btn_forgot = (Button) layout.findViewById(R.id.btn_forgot);
        BtnCancel = (Button) layout.findViewById(R.id.btn_cancel);
        btn_forgot.setOnClickListener(this);
        BtnCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dissmiss();
                break;
            case R.id.btn_forgot:
                //点击删除已连接过的wifi配置
                WifiUtils.getInstance(context).disconnectWifi(scanResult.SSID);
                WifiUtils.getInstance(context).delWifiConfig(scanResult.SSID);
                dissmiss();
                break;
        }

    }

    ScanResult scanResult;
    public void show(ScanResult scanResult) {
        this.scanResult = scanResult;
        WifiInfo info = WifiUtils.getInstance(context).getConnectedInfo();
        txtWifiName.setText("WiFiName : " + scanResult.SSID);
        tv_wifi_safe.setText("" + scanResult.capabilities.toUpperCase());
        tv_wifi_level.setText(WifiUtils.getInstance(context).singLevToStr(scanResult.level));
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
