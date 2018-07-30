package com.reeman.wifi.wifimodule.dailog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.reeman.wifi.wifimodule.R;
import com.reeman.wifi.wifimodule.utils.WifiUtils;


public class WifiAddDialog {
    Context context;
    Dialog mDialog;
    private Spinner sp_safe;
    String[] arrays = new String[]{"无", "WEP", "WEP/WPA2 PSK", "802.1x EAP", "WAPI PSK", "WAPI SERT"};

    public WifiAddDialog(final Context context) {
        this.context = context;
        mDialog = new Dialog(context, R.style.MyDialog);
        final View layout = View.inflate(context, R.layout.dialog_add_wifi, null);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(layout, new ViewGroup.LayoutParams(800, 500));
        initView(layout);
        initData();
    }

    private EditText edtPassword, et_wifiName;
    Button BtnCancel, BtnConn;

    private void initView(View layout) {
        et_wifiName = (EditText) layout.findViewById(R.id.et_wifiname);
        edtPassword = (EditText) layout.findViewById(R.id.et_password);
        sp_safe = (Spinner) layout.findViewById(R.id.sp_safe);
        BtnCancel = (Button) layout.findViewById(R.id.btn_cancel);
        BtnConn = (Button) layout.findViewById(R.id.btn_connect);
        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.spinner_safe,
                context.getResources().getStringArray(R.array.safe_choise));
        sp_safe.setAdapter(adapter);
        sp_safe.setSelection(2, true);
        sp_safe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                safe_desc = arrays[position].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_wifiName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyBord(v);
                } else {
                    hiddleBord(v);
                }
            }
        });

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyBord(v);
                } else {
                    hiddleBord(v);
                }
            }
        });

    }

    public void showKeyBord(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    public void hiddleBord(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
    }


    String safe_desc = "WEP/WPA2 PSK";
    String wifiName;
    String password;

    private void initData() {
        BtnConn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkWifi()) {
                    WifiUtils.WifiCipherType type = null;
                    if (safe_desc.contains("WPA")) {
                        type = WifiUtils.WifiCipherType.WIFICIPHER_WPA;
                    } else if (safe_desc.contains("WEP")) {
                        type = WifiUtils.WifiCipherType.WIFICIPHER_WEP;
                    } else if (safe_desc.contains("WEP") && safe_desc.contains("WPA")) {
                        type = WifiUtils.WifiCipherType.WIFICIPHER_WPA;
                    } else {
                        type = WifiUtils.WifiCipherType.WIFICIPHER_NOPASS;
                    }
                    WifiUtils.getInstance(context).connect(wifiName,password,type);
                    dissmiss();
                }
            }
        });
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dissmiss();
            }
        });
    }

    public boolean checkWifi() {
        wifiName = et_wifiName.getText().toString().trim();
        password = edtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(wifiName)) {
//            showToast("请输入需要连接的wifi名称");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 8) {
//            showToast("请输入正确的密码 ！");
            return false;
        }
        return true;
    }


    public void show() {
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


    public String getWifiName() {
        return et_wifiName.getText().toString().trim();
    }

}
