package com.reeman.wifi.wifimodule.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * Created by reeman on 2018/7/28.
 */

public class NetworkReceiver extends BroadcastReceiver {
    private Context mContext;
    private Handler mHandler;

    //注册广播
    public void initRegister(Context context,Handler handler){
        mContext = context;
        mHandler = handler;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(this,filter);
    }

    //广播释放
    public void unRegister(Context context,BroadcastReceiver broadcastReceiver){
        if (broadcastReceiver == null){
            context.unregisterReceiver(broadcastReceiver);
        }
    }


    public static final int NETWORK_STATE_CHANGED_ACTION = 2001;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch(action){
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                //监听WiFi的连接状态 一共有五种状态，可从networkInfo中获得 一般获取后用于刷新连接状态
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Message message = Message.obtain();
                message.what = NETWORK_STATE_CHANGED_ACTION;
                message.obj = info;
                mHandler.sendMessage(message);
                break;

            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                //wifi开关广播

                break;

            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                //wifi扫描结果广播
                break;

            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                //wifi 连接认证失败
                int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                if (error == WifiManager.ERROR_AUTHENTICATING){
                    Message message1 = Message.obtain();
                    message1.what = WifiManager.ERROR_AUTHENTICATING;
                    message1.obj = error;
                    mHandler.sendMessage(message1);
                }


                break;

            default:
                break;
        }

    }

}
