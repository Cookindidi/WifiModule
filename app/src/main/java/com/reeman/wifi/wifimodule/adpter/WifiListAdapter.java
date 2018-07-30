package com.reeman.wifi.wifimodule.adpter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reeman.wifi.wifimodule.R;
import com.reeman.wifi.wifimodule.utils.WifiUtils;

import java.util.List;

/**
 * Created by reeman on 2018/7/28.
 */
public class WifiListAdapter extends BaseAdapter {

    private List<ScanResult> datas;
    private Context context;
    // 取得WifiManager对象
    private WifiManager mWifiManager;
    private ConnectivityManager cm;

    public void setDatas(List<ScanResult> datas) {
        this.datas = WifiUtils.getInstance(context).checkReSsid(datas);
    }

    public WifiListAdapter(Context context, List<ScanResult> datas) {
        super();
        this.datas = datas;
        this.context = context;
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder tag = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_wifi_list, null);
            tag = new Holder();
            tag.txtWifiName = (TextView) convertView
                    .findViewById(R.id.txt_wifi_name);
            tag.txtWifiDesc = (TextView) convertView
                    .findViewById(R.id.txt_wifi_desc);
            tag.imgWifiLevelIco = (ImageView) convertView
                    .findViewById(R.id.img_wifi_level_ico);
            tag.img_wifi_lock = (ImageView) convertView
                    .findViewById(R.id.img_wifi_lock);
            convertView.setTag(tag);
        }
        // 设置数据
        Holder holder = (Holder) convertView.getTag();
        String desc = "";
        String descOri = datas.get(position).capabilities;

        String ssid = datas.get(position).SSID;
        if (descOri.toUpperCase().contains("WPA-PSK")) {
            desc = "（可使用 WPA）";
        }
        if (descOri.toUpperCase().contains("WPA2-PSK")) {
            desc = "（可使用 WPA2）";
        }
        if (descOri.toUpperCase().contains("WPA-PSK")
                && descOri.toUpperCase().contains("WPA2-PSK")) {
            desc = "（可使用 WPA/WPA2）";
        }

        //显示是否已保存
        if (WifiUtils.getInstance(context).isConfiguration(ssid) != -1){
            if (descOri.toUpperCase().contains("WPA")){
                desc = "已保存，加密";
            }else {
                desc = "已保存，无密码";
            }
        }else {
            if (!desc.contains("WPA")){
                desc = "无密码";
            }else {
                desc = "加密" + desc;
            }
        }

        int level = datas.get(position).level;
        updateWifiImg(level, holder.imgWifiLevelIco);
        updateLock(desc, holder.img_wifi_lock);

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String g1 = wifiInfo.getSSID();
        String g2 = "\"" + datas.get(position).SSID + "\"";
        holder.txtWifiDesc.setTextColor(context.getResources().getColor(R.color.white));
        holder.txtWifiName.setTextColor(context.getResources().getColor(R.color.white));
        NetworkInfo.State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (wifi == NetworkInfo.State.CONNECTING) {
            if (g2.endsWith(g1)) {
                desc = "连接中...";
            }
        } else if (wifi == NetworkInfo.State.CONNECTED) {
            if (g2.endsWith(g1)) {
                desc = "已连接";
                holder.txtWifiDesc.setTextColor(context.getResources().getColor(R.color.blue));
                holder.txtWifiName.setTextColor(context.getResources().getColor(R.color.blue));
            }
        }
        holder.txtWifiName.setText(datas.get(position).SSID);
        holder.txtWifiDesc.setText(desc);
//        holder.txtWifiDesc.setText(descOri);
        return convertView;
    }


    /***
     * 更新是否需要密碼的圖標
     * @param isLock
     * @param imgLock
     */
    public void updateLock(String isLock, ImageView imgLock) {
        if (isLock.contains("无密码")) {
            imgLock.setVisibility(View.GONE);
        } else {
            imgLock.setVisibility(View.VISIBLE);
        }

    }


    /***
     * 更新wifi图标
     * @param level
     * @param wifiImage
     */
    public void updateWifiImg(int level, ImageView wifiImage) {
        int imgId = R.drawable.wifi_4;
        if (Math.abs(level) < 51) { //极强
            imgId = R.drawable.wifi_4;
        } else if (Math.abs(level) > 50 && Math.abs(level) < 61) {
            imgId = R.drawable.wifi_3;
        } else if (Math.abs(level) > 60 && Math.abs(level) < 71) {
            imgId = R.drawable.wifi_2;
        } else if (Math.abs(level) > 70 && Math.abs(level) < 85) {
            imgId = R.drawable.wifi_1;
        } else if (Math.abs(level) > 84) {
            imgId = R.drawable.wifi_1;
        }
        wifiImage.setImageResource(imgId);
    }


    public static class Holder {
        public TextView txtWifiName;
        public TextView txtWifiDesc;
        public ImageView imgWifiLevelIco;
        ImageView img_wifi_lock;
    }
}
