package com.reeman.wifi.wifimodule.dailog;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reeman.wifi.wifimodule.R;
import com.reeman.wifi.wifimodule.utils.WifiUtils;


public class WifiConnectDialog {

    Context context;
    Dialog mDialog;
    String TAG = "wifi";
    Handler mHandler;

    public WifiConnectDialog(final Context context, Handler handler) {
        mHandler=handler;
        this.context = context;
        mDialog = new Dialog(context, R.style.MyDialog);
        final View layout = View.inflate(context, R.layout.view_wifi_conn, null);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(layout, new ViewGroup.LayoutParams(600, 400));
        initView(layout);
        initData();
    }

    private EditText edtPassword;
    TextView txtWifiName, txtSinglStrength, txtSecurityLevel,tv_password;
    Button BtnCancel, BtnConn,BtnCancelSave;
    CheckBox cb_show_psw;

    private void initView(View layout) {
        edtPassword = (EditText) layout.findViewById(R.id.edt_password);
        //======================================
        txtWifiName = (TextView) layout.findViewById(R.id.txt_wifi_name);
        txtSinglStrength = (TextView) layout.findViewById(R.id.txt_signal_strength);
        txtSecurityLevel = (TextView) layout.findViewById(R.id.txt_security_level);
        tv_password = (TextView) layout.findViewById(R.id.txt_password);
        BtnCancel = (Button) layout.findViewById(R.id.btn_cancel);
        BtnConn = (Button) layout.findViewById(R.id.btn_connect);
        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyBord(v);
                }
            }
        });
        //取消保存
        BtnCancelSave = (Button) layout.findViewById(R.id.btn_cancel_save);

        //显示密码
        cb_show_psw = (CheckBox) layout.findViewById(R.id.cb_show_psw);
    }


    public void showKeyBord(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    public static final int LINE_WIFI_ERROR = 2345;
    public static final int LINE_WIFI_SUCCESS = LINE_WIFI_ERROR + 1;
    public static final int WIFI_CANCEL_SAVE = LINE_WIFI_SUCCESS + 1;

    private void initData() {
        BtnConn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hasConnected){
                    boolean isCon = WifiUtils.getInstance(context).connectSavedWifi(WifiUtils.getInstance(context).isConfigured(scanResult.SSID).networkId);
                    Log.i("ggg","连接接结果：" + isCon);
                    if (isCon){
                        mHandler.sendEmptyMessage(WifiUtils.LINE_WIFI_SUCCESS);
                    }else {
                        mHandler.sendEmptyMessage(WifiUtils.LINE_WIFI_ERROR);
                    }
                    Log.i("ggg","连接接结果：");
                }else {
                    String password = getPassword();
                    if (TextUtils.isEmpty(password) || password.length() < 8) {
                        Toast.makeText(context, "请输入大于8位数的密码",Toast.LENGTH_LONG).show();
                        return;
                    }

                    boolean isCon = WifiUtils.getInstance(context).connectUnSaveWifi(scanResult,getPassword());
                    Log.i("ggg","连接接结果：" + isCon);
                    if (isCon){
                        mHandler.sendEmptyMessage(WifiUtils.LINE_WIFI_SUCCESS);
                    }else {
                        mHandler.sendEmptyMessage(WifiUtils.LINE_WIFI_ERROR);
                    }
                }
                dissmiss();
            }
        });
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dissmiss();
            }
        });

        BtnCancelSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击删除已连接过的wifi配置
                boolean isCancel = WifiUtils.getInstance(context).delWifiConfig(scanResult.SSID);
                if (isCancel){
                    mHandler.sendEmptyMessage(WIFI_CANCEL_SAVE);
                }
                dissmiss();
            }
        });

        cb_show_psw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //如果选中，显示密码
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }


    ScanResult scanResult;
    boolean hasConnected;
    public void show(ScanResult scanResult) {
        try {

            hasConnected= false;
            //隐藏密码
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            WifiConfiguration cfg = WifiUtils.getInstance(context).isConfigured(scanResult.SSID);
            if (cfg != null){
                edtPassword.setVisibility(View.GONE);
                tv_password.setVisibility(View.GONE);
                BtnCancelSave.setVisibility(View.VISIBLE);

                hasConnected = true;
                cb_show_psw.setVisibility(View.GONE);
            }else {
                edtPassword.setVisibility(View.VISIBLE);
                tv_password.setVisibility(View.VISIBLE);
                BtnCancelSave.setVisibility(View.GONE);
                cb_show_psw.setVisibility(View.VISIBLE);
                cb_show_psw.setChecked(false);

            }
            Log.i("ggg","点击SSID：" + scanResult.SSID  + "  |cfg:" + cfg.SSID + "  |hasConnected:"+ hasConnected);
        } catch (Exception e) {
            Log.i("ggg","报错：" + e.toString());
            e.printStackTrace();
        }
        //========機智的分割線==============
        this.scanResult = scanResult;
        txtWifiName.setText("  " + scanResult.SSID);
        txtSinglStrength.setText("  " + WifiUtils.getInstance(context).singLevToStr(scanResult.level));
        txtSecurityLevel.setText("  " + scanResult.capabilities.toUpperCase());
        edtPassword.setText("");
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

    public String getPassword() {
        return edtPassword.getText().toString().trim();
    }


}
