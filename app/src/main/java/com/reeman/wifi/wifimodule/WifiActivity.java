package com.reeman.wifi.wifimodule;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WifiActivity extends BaseActivity{

    private ViewController mViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        if (checkPermission()){
            Log.i("ggg","已获得权限");
            mViewController = new ViewController(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllPermission = true;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;   //判断用户是否同意获取权限
                    break;
                }
            }

            //如果同意权限
            if (hasAllPermission) {
                Log.i("ggg","权限获取成功！");
                mHasPermission = true;
                mViewController = new ViewController(this);

            } else {  //用户不同意权限
                mHasPermission = false;
                Toast.makeText(WifiActivity.this,"获取权限失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
