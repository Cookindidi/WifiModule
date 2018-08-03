package com.reeman.wifi.wifimodule;

import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.reeman.wifi.wifimodule.adpter.WifiLinkedAdapter;
import com.reeman.wifi.wifimodule.adpter.WifiListAdapter;
import com.reeman.wifi.wifimodule.dailog.WifiAddDialog;
import com.reeman.wifi.wifimodule.dailog.WifiConNoPswDialog;
import com.reeman.wifi.wifimodule.dailog.WifiConnectDialog;
import com.reeman.wifi.wifimodule.dailog.WifiHasLinkedDialog;
import com.reeman.wifi.wifimodule.receiver.NetworkReceiver;
import com.reeman.wifi.wifimodule.utils.WifiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reeman on 2018/7/28.
 */

public class ViewController implements View.OnClickListener{
    private WifiActivity mContext;

    // 扫描结果列表
    private List<ScanResult> wifiList = new ArrayList<>();
    private List<ScanResult> wifiLinkedList = new ArrayList<>();

    // 显示列表adapter
    private WifiListAdapter wifiListAdapter;
    private WifiLinkedAdapter wifiLinkedAdapter;

    //显示列表对象
    private ListView wifiListView;
    private ListView wifiLinkedView;

    Button refresh_list_btn, btn_add_wifi;
    NetworkReceiver mBroadcastReceiver;

    WifiConnectDialog wifiConnectionDialog;
    WifiConNoPswDialog mWifiConNoPassDialog;
    WifiHasLinkedDialog mWifiStateAlertDialog;
    WifiAddDialog mWifiAddDialog;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NetworkReceiver.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo info = (NetworkInfo) msg.obj;
                    Log.i("ggg","收到网络状态变化：" +info.getState());
                    if (info.getState().equals(NetworkInfo.State.DISCONNECTED)
                            || info.getState().equals(NetworkInfo.State.CONNECTING)
                            || info.getState().equals(NetworkInfo.State.CONNECTED)) {
                        //網絡斷開,正在连接
                        initWifiListData();
                    }
                    break;

                case WifiManager.ERROR_AUTHENTICATING:
                    //认证错误，删除已保存的错误密码
                    if (scanResult != null){
                        WifiUtils.getInstance(mContext).delWifiConfig(scanResult.SSID);
                        Log.d("ggg", "密码认证错误,删除保存的错误密码 "+ scanResult.SSID);
                        Toast.makeText(mContext,"密码认证错误,请重新输入密码！",Toast.LENGTH_SHORT).show();
                        initWifiListData();
                    }
                    break;

                case WifiConnectDialog.WIFI_CANCEL_SAVE:
                    //删除保存
                    Log.i("ggg","删除保存");
                    initWifiListData();
                    break;

                case WifiUtils.LINE_WIFI_ERROR:
                    Toast.makeText(mContext,"WIFI连接异常，跳转至系统WIFI",Toast.LENGTH_LONG).show();
                    if (scanResult != null){
                        WifiUtils.getInstance(mContext).delWifiConfig(scanResult.SSID);
                    }
                    startSetting();
                    break;

            }
        }
    };

    /***进入设置界面*/
    public void startSetting() {
        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
        mContext.startActivity(wifiSettingsIntent);
    }

    public ViewController(WifiActivity context) {
        mContext = context;
        initView();
        initWifiListData();
        initOnItemClickListener();
    }

    private void initView(){
        btn_add_wifi = (Button) mContext.findViewById(R.id.btn_add_wifi);
        btn_add_wifi.setOnClickListener(this);

        refresh_list_btn = (Button) mContext.findViewById(R.id.refresh_list_btn);
        refresh_list_btn.setOnClickListener(this);

        wifiListView = mContext.findViewById(R.id.wifi_list);
        wifiListAdapter = new WifiListAdapter(mContext,wifiList);
        wifiListView.setAdapter(wifiListAdapter);

        wifiLinkedView = (ListView) mContext.findViewById(R.id.wifi_link_alread);
        wifiLinkedAdapter = new WifiLinkedAdapter(mContext, wifiLinkedList);
        wifiLinkedView.setAdapter(wifiLinkedAdapter);

        wifiConnectionDialog = new WifiConnectDialog(mContext,handler);
        mWifiConNoPassDialog = new WifiConNoPswDialog(mContext,handler);
        mWifiStateAlertDialog = new WifiHasLinkedDialog(mContext);
        mWifiAddDialog = new WifiAddDialog(mContext,handler);

        mBroadcastReceiver = new NetworkReceiver();
        mBroadcastReceiver.initRegister(mContext,handler);

    }

    private void initWifiListData(){

        wifiList.clear();
        wifiLinkedList.clear();

        wifiList = WifiUtils.getInstance(mContext).getScanResults();
        wifiList = WifiUtils.getInstance(mContext).sortScanResult(wifiList);

        wifiLinkedList = WifiUtils.getInstance(mContext).getConnectedWifiScanResult();

        wifiListAdapter.setDatas(wifiList);
        wifiLinkedAdapter.setDatas(wifiLinkedList);

        wifiListAdapter.notifyDataSetChanged();
        wifiLinkedAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.refresh_list_btn){
            Log.i("ggg","点击刷新");
            initWifiListData();
        }else if (view.getId() == R.id.btn_add_wifi){
            Log.i("ggg","点击增加wifi");
            mWifiAddDialog.show();
        }

    }


    ScanResult scanResult;
    public void initOnItemClickListener(){

        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    scanResult = wifiList.get(i);
                    String desc = "";
                    String descOri = scanResult.capabilities;
                    if (descOri.toUpperCase().contains("WPA-PSK")) {
                        desc = "WPA";
                    }
                    if (descOri.toUpperCase().contains("WPA2-PSK")) {
                        desc = "WPA2";
                    }
                    if (descOri.toUpperCase().contains("WPA-PSK")
                            && descOri.toUpperCase().contains("WPA2-PSK")) {
                        desc = "WPA/WPA2";
                    }
                    if (descOri.toUpperCase().contains("WEP")) {
                        desc = "（已通过WEP保护）";
                    }
                    if (desc.equals("")) {
                        connectSelf(scanResult);
                        return;
                    }
                    connect(scanResult);
                } catch (Exception e) {
                }
            }
        });

        wifiLinkedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                scanResult = wifiLinkedList.get(i);
                connect(scanResult);
            }});
    }

    /**
     * 有密码验证连接
     * @param scanResult
     */
    private void connect(ScanResult scanResult) {
        if (WifiUtils.getInstance(mContext).isConnected(scanResult)) {
            //已經連接，查看網絡狀態
            Log.i("ggg","点击已连接的wifi：" + scanResult.SSID);
            mWifiStateAlertDialog.show(scanResult);
        } else {
            //显示对话框
            wifiConnectionDialog.show(scanResult);
            Log.i("ggg","点击未连接的wifi：" + scanResult.SSID);
        }
    }

    /**
     * 无密码直连
     * @param scanResult
     */
    private void connectSelf(ScanResult scanResult) {
        Log.i("ggg","点击无密码的wifi：" + scanResult.SSID);
        mWifiConNoPassDialog.show(scanResult);

    }

}
