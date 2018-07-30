package com.reeman.wifi.wifimodule.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by reeman on 2018/7/27.
 */

public class WifiUtils {
    private Context mContext;
    public static WifiManager mWifiManager;
    private static WifiUtils instance;
    private static ConnectivityManager mConnectivityManager ;

    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WifiCipherType, WIFICIPHER_INVALID
    }

    private WifiUtils(){}

    /**
     * 获取WifiUtils 实例
     * 获取WifiManager实例
     * @param context
     * @return
     */
    public static synchronized WifiUtils getInstance(Context context){
        if (instance == null){
            instance = new WifiUtils();
        }
        if (mWifiManager == null){
            mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        if (mConnectivityManager == null){
            mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        instance.openWifi();
        return instance;
    }

    /**
     * 检查wifi状态
     * @return
     * Wifi 的状态目前有五种：正开启，开启，正关闭，关闭，未知
     */
    public int checkWifiState(){
        return mWifiManager.getWifiState();
    }

    public void openWifi(){
        if (!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }

    public void closeWifi(){
        if (mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
        }
    }

    public void startScanWifi(){
        mWifiManager.startScan();
    }

    public List<ScanResult> getScanResults(){
        startScanWifi();
        return  mWifiManager.getScanResults();
    }

    public WifiInfo getConnectedInfo(){
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo;
    }

    public List<WifiConfiguration> getConfigureNetworks(){
        startScanWifi();
        return mWifiManager.getConfiguredNetworks();
    }

    public boolean isNetworkConnected() {
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    /**
     * @param ssid 根据ssid 获取 networkId
     * @return 已经配置直接return id 没配置返回值为-1
     */
    public int ssidToNetworkId(String ssid){
        List<WifiConfiguration> configurationList = getConfigureNetworks();
        for (int i = 0;i<configurationList.size();i++){
            if (configurationList.get(i).SSID.equals("\""+ssid+"\"")){
                return configurationList.get(i).networkId;
            }
        }
        return -1;
    }

    public int isConfiguration(String ssid){
        List<WifiConfiguration> configurationList = getConfigureNetworks();
        for (int i = 0;i<configurationList.size();i++){
            if (configurationList.get(i).SSID.equals("\""+ssid+"\"")){
                return configurationList.get(i).networkId;
            }
        }
        return -1;
    }

    /**
     * Function:判断扫描结果是否连接上<br>
     *
     * @param result
     * @return<br>
     */
    public boolean isConnected(ScanResult result) {
        if (result == null) {
            return false;
        }
        String g2 = "\"" + result.SSID + "\"";
        if (mWifiManager.getConnectionInfo().getSSID() != null && mWifiManager.getConnectionInfo().getSSID().endsWith(g2)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param networkId
     * @return
     */
    public boolean connectWifiByNetworkId(int networkId){
        List<WifiConfiguration> configurationList = getConfigureNetworks();
        for (int i = 0; i< configurationList.size();i++){
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == networkId){
                while (!(mWifiManager.enableNetwork(networkId,true))){}
                return true;
            }
        }
        return false;
    }

    /**
     * 根据ssid 断开网络
     * @param ssid
     */
    public void disconnectWifi(String ssid) {
        int networkId = ssidToNetworkId(ssid);
        mWifiManager.disableNetwork(networkId);
        mWifiManager.disconnect();
    }

    /**
     * Function:信号强度转换为字符串<br>
     *
     * @param level <br>
     * @author ZYT DateTime 2014-5-14 下午2:14:42<br>
     */
    public String singLevToStr(int level) {
        String resuString = "无信号";
        if (Math.abs(level) < 51) { //极强
            resuString = "极强";
        } else if (Math.abs(level) > 50 && Math.abs(level) < 61) {
            resuString = "较强";
        } else if (Math.abs(level) > 60 && Math.abs(level) < 71) {
            resuString = "弱";
        } else if (Math.abs(level) > 70 && Math.abs(level) < 85) {
            resuString = "非常弱";
        } else if (Math.abs(level) > 84) {
            resuString = "极弱";
        } else {
            resuString = "无信号";
        }
        Log.e("ggg", "==当前信号强度==" + level + "//" + resuString);
        return resuString;
    }

    public boolean connectSpecificAP(ScanResult scan) {
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        boolean networkInSupplicant = false;
        boolean connectResult = false;
        // 重新连接指定AP
        mWifiManager.disconnect();
        for (WifiConfiguration w : list) {
            // 将指定AP 名字转化
            // String str = convertToQuotedString(info.ssid);
            if (w.BSSID != null && w.BSSID.equals(scan.BSSID)) {
                connectResult = mWifiManager.enableNetwork(w.networkId, true);
                // mWifiManager.saveConfiguration();
                networkInSupplicant = true;
                break;
            }
        }
        if (!networkInSupplicant) {
            WifiConfiguration config = CreateWifiInfo(scan, "");
            connectResult = addNetwork(config);
        }

        return connectResult;
    }

    /**
     * 添加到网络
     *
     * @param wcg
     * @author Xiho
     */
    public boolean addNetwork(WifiConfiguration wcg) {
        if (wcg == null) {
            return false;
        }
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        mWifiManager.saveConfiguration();
        System.out.println(b);
        return b;
    }

    // 然后是一个实际应用方法，只验证过没有密码的情况：
    public WifiConfiguration CreateWifiInfo(ScanResult scan, String Password) {
        WifiConfiguration config = new WifiConfiguration();
        config.hiddenSSID = false;
        config.status = WifiConfiguration.Status.ENABLED;

        if (scan.capabilities.contains("WEP")) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);

            config.SSID = "\"" + scan.SSID + "\"";

            config.wepTxKeyIndex = 0;
            config.wepKeys[0] = Password;
            // config.preSharedKey = "\"" + SHARED_KEY + "\"";
        } else if (scan.capabilities.contains("PSK")) {
            //
            config.SSID = "\"" + scan.SSID + "\"";
            config.preSharedKey = "\"" + Password + "\"";
        } else if (scan.capabilities.contains("EAP")) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.SSID = "\"" + scan.SSID + "\"";
            config.preSharedKey = "\"" + Password + "\"";
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.SSID = "\"" + scan.SSID + "\"";
            config.preSharedKey = null;
        }
        return config;
    }

    /**
     * 根据ssid pwd 添加到系统wifi配置中
     * @param ssid
     * @param pwd 可为空，表示不加密wifi
     * @return 配置好则返回networkId 否则-1
     */
    public int addWifiConfig(String ssid,String pwd){
        int wifiId = -1;
        int wifiId2 = -1;
        List<ScanResult> scanResults = getScanResults();
        Log.i("ggg", "pwd :"+pwd);
        for(int i = 0;i < scanResults.size(); i++){
            ScanResult wifi = scanResults.get(i);
            if(wifi.SSID.equals(ssid)){
                if(pwd!=null){
                    Log.i("ggg","AddWifiConfig()   777777 equals");
                    WifiConfiguration wifiCong = new WifiConfiguration();
                    wifiCong.SSID = "\""+wifi.SSID+"\"";//\"ת���ַ���"
                    wifiCong.preSharedKey = "\""+pwd+"\"";//WPA-PSK����
                    wifiCong.hiddenSSID = false;
                    wifiCong.status = WifiConfiguration.Status.ENABLED;
                    wifiId = mWifiManager.addNetwork(wifiCong);//�����úõ��ض�WIFI������Ϣ���,�����ɺ�Ĭ���ǲ�����״̬���ɹ�����ID������Ϊ-1
                    Log.i("ggg", "wifiId: "+wifiId);
                    if(wifiId != -1){
                        mWifiManager.enableNetwork(wifiId, true);
                        mWifiManager.saveConfiguration();
                        mWifiManager.reconnect();
                        return wifiId;
                    }
                }else{
                    Log.i("ggg","AddWifiConfig()   6666 equals");
                    WifiConfiguration config = new WifiConfiguration();
                    config.allowedAuthAlgorithms.clear();
                    config.allowedGroupCiphers.clear();
                    config.allowedKeyManagement.clear();
                    config.allowedPairwiseCiphers.clear();
                    config.allowedProtocols.clear();
                    config.SSID = "\"" +wifi.SSID + "\"";
                    // 没有密码
//	              config.wepKeys[0] = "";
                    config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//	              config.wepTxKeyIndex = 0;
                    wifiId2 = mWifiManager.addNetwork(config);
                    Log.i("ggg", "wifiId2: "+wifiId2);
                    if(wifiId2 != -1){
                        mWifiManager.enableNetwork(wifiId2, true);
                        mWifiManager.saveConfiguration();
                        mWifiManager.reconnect();
                        return wifiId2;
                    }
                }
            }
        }
        return wifiId;
    }

    /**
     * 给外部提供一个借口，连接无线网络
     *
     * @param SSID
     * @param Password
     * @param Type
     * @return true:连接成功；false:连接失败<br>
     */
    public boolean connect(String SSID, String Password, WifiCipherType Type) {
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (Exception ie) {
            }
        }
        if (SSID == null || Password == null || SSID.equals("")) {
            Log.e(this.getClass().getName(),
                    "addNetwork() ## nullpointer error!");
            return false;
        }
        WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, Type);
        // wifi的配置信息
        if (wifiConfig == null) {
            return false;
        }
        // 查看以前是否也配置过这个网络
        WifiConfiguration tempConfig = isConfigured(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        // 添加一个新的网络描述为一组配置的网络。
        int netID = mWifiManager.addNetwork(wifiConfig);
        Log.d("ggg", "wifi的netID为：" + netID);
        // 断开连接
        mWifiManager.disconnect();
        // 重新连接
        Log.d("ggg", "Wifi的重新连接netID为：" + netID);
        // 设置为true,使其他的连接断开
        boolean mConnectConfig = mWifiManager.enableNetwork(netID, true);
        mWifiManager.reconnect();
        return mConnectConfig;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password,
                                             WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type ==WifiCipherType.WIFICIPHER_WPA) {
            // 修改之后配置
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);

        } else {
            return null;
        }
        return config;
    }

    /**
     * 添加隐藏的wifi
     * @param ssid
     * @param pwd
     * @return
     */
    public int addHideWifiConfig(String ssid,String pwd){
        int wifiId = -1;
        int wifiId2 = -1;
        if(pwd!=null){
            Log.i("zhangjie","AddHideWifiConfig()   999 equals");
            WifiConfiguration wifiCong = new WifiConfiguration();
            wifiCong.SSID = "\""+ssid+"\"";//\"ת���ַ���"
            wifiCong.preSharedKey = "\""+pwd+"\"";//WPA-PSK����
            wifiCong.hiddenSSID = true;
            wifiCong.status = WifiConfiguration.Status.ENABLED;
            wifiId = mWifiManager.addNetwork(wifiCong);//�����úõ��ض�WIFI������Ϣ���,�����ɺ�Ĭ���ǲ�����״̬���ɹ�����ID������Ϊ-1
            Log.i("zhangjie", "wifiId: "+wifiId);
            if(wifiId != -1){
                mWifiManager.enableNetwork(wifiId, true);
                mWifiManager.saveConfiguration();
                mWifiManager.reconnect();
                return wifiId;
            }
        }else{
            Log.i("zhangjie","AddHideWifiConfig()   000 equals");
            WifiConfiguration config = new WifiConfiguration();
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            config.SSID = "\"" +ssid + "\"";
            config.hiddenSSID = true;
            // 没有密码
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiId2 = mWifiManager.addNetwork(config);
            Log.i("zhangjie", "wifiId2: "+wifiId2);
            if(wifiId2 != -1){
                mWifiManager.enableNetwork(wifiId2, true);
                mWifiManager.saveConfiguration();
                mWifiManager.reconnect();
                return wifiId2;
            }
        }
        return wifiId;
    }

    //删除配置信息
    public boolean delWifiConfig(String ssid){
        boolean isDel = false;
        List<WifiConfiguration> wifiConfigList = getConfigureNetworks();
        for(int i = 0; i < wifiConfigList.size(); i++){
            if(wifiConfigList.get(i).SSID.equals("\""+ssid+"\"")){
                mWifiManager.removeNetwork(wifiConfigList.get(i).networkId);
                mWifiManager.saveConfiguration();
                isDel = true;
                return isDel;
            }
        }
        return isDel;
    }

    // 查看以前是否也配置过这个网络
    public WifiConfiguration isConfigured(String SSID) {
        List<WifiConfiguration> existingConfigs = getConfigureNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 获取已连接的wifi
     * @return
     */
    public List<ScanResult> getConnectedWifiScanResult(){
        List<ScanResult> scanR = new ArrayList<>();
        List<ScanResult> scanResults = getScanResults();
        for (int i = 0;i<scanResults.size();i++){
            String wifiName = scanResults.get(i).SSID.trim();
            if (!TextUtils.isEmpty(wifiName) || wifiName.length() > 2){
                ScanResult scanResult = scanResults.get(i);
                if (isConfigured(scanResult.SSID) != null
                        && isConfigured(scanResult.SSID).networkId == getConnectedInfo().getNetworkId()){
                    scanR.add(scanResult);
                }
            }
        }
        return scanR;
    }

    /**
     * 过滤重复，已连接的wifi，并根据强度进行排序
     * @param datas
     * @return
     */
    public List<ScanResult> checkReSsid(List<ScanResult> datas){
        List<ScanResult> sResults = datas;
        //排除重复的
        for (int i=0;i<datas.size();i++){
            //排除已连接的
            if (isConfigured(sResults.get(i).SSID) != null
                    && isConfigured(sResults.get(i).SSID).networkId == getConnectedInfo().getNetworkId()){
                sResults.remove(sResults.get(i));
                continue;
            }

            for (int j=i+1;j<datas.size();j++){
                if (datas.get(i).SSID.equals(datas.get(j).SSID)){
                    sResults.remove(datas.get(j));
                }
            }
        }

        //根据信号强弱排序
        Collections.sort(sResults, new Comparator<ScanResult>() {

            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return rhs.level - lhs.level;
            }
        });
        return sResults;
    }


}
