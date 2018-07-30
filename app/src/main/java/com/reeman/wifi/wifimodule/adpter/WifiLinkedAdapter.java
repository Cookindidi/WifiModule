package com.reeman.wifi.wifimodule.adpter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.util.Log;
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
public class WifiLinkedAdapter extends BaseAdapter {

    private List<ScanResult> datas;
    private Context context;
    // 取得WifiManager对象
    private ConnectivityManager cm;

    public void setDatas(List<ScanResult> datas) {
        this.datas = datas;
    }

    public WifiLinkedAdapter(Context context, List<ScanResult> datas) {
        super();
        this.datas = datas;
        this.context = context;
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
        if (WifiUtils.getInstance(context).isConfiguration(ssid)!= -1){
            desc = "已保存，加密";
        }else {
            Log.i("ggg", "未保存的desc:" +desc);
            if (!desc.contains("WPA")){
                desc = "无密码";
            }else {
                desc = "加密" + desc;
            }
        }

        int level = datas.get(position).level;
        updateWifiImg(level, holder.imgWifiLevelIco);
        updateLock(desc, holder.img_wifi_lock);
        holder.txtWifiDesc.setTextColor(context.getResources().getColor(R.color.blue));
        holder.txtWifiName.setTextColor(context.getResources().getColor(R.color.blue));
        NetworkInfo.State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (wifi == NetworkInfo.State.CONNECTING) {
            desc = "连接中...";
        } else if (wifi == NetworkInfo.State.DISCONNECTED) {
            desc = "网络切换中";
        } else if (wifi == NetworkInfo.State.DISCONNECTING) {
            desc = "网络切换中";
        } else if (wifi == NetworkInfo.State.SUSPENDED) {
            desc = "连接中...";
        } else if (wifi == NetworkInfo.State.CONNECTED) {
            desc = "已连接";
        } else if (wifi == NetworkInfo.State.UNKNOWN) {
            desc = "网络异常,请在高级设置，进入高级系统设置";
        }
        holder.txtWifiName.setText(datas.get(position).SSID);
        holder.txtWifiDesc.setText(desc);
        return convertView;
    }

    /***
     * 更新是否需要密碼的圖標
     * @param isLock
     * @param imgLock
     */
    public void updateLock(String isLock, ImageView imgLock) {
        if (TextUtils.isEmpty(isLock)) {
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
        if (Math.abs(level) > 100) {
            imgId = R.drawable.wifi_1;
        } else if (Math.abs(level) > 80) {
            imgId = R.drawable.wifi_2;
        } else if (Math.abs(level) > 70) {
            imgId = R.drawable.wifi_3;
        } else if (Math.abs(level) > 60) {
            imgId = R.drawable.wifi_3;
        } else if (Math.abs(level) > 50) {
            imgId = R.drawable.wifi_4;
        } else {
            imgId = R.drawable.wifi_4;
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
