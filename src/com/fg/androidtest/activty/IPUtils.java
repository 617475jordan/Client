package com.fg.androidtest.activty;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by zheng on 2017/3/2.
 */
public class IPUtils {
    /**
     * ��ȡ����IPv4��ַ
     *
     * @param context
     * @return ����IPv4��ַ��null������������
     */
    public static String getLocalIpAddress(Context context) {
        // ��ȡWiFi����
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // �ж�WiFi�Ƿ���
        if (wifiManager.isWifiEnabled()) {
            // �Ѿ�������WiFi
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp0(ipAddress);
            return ip;
        } else {
            // δ����WiFi
            return null;

        }
    }
    public static String getServerIpAddress(Context context) {
        // ��ȡWiFi����
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // �ж�WiFi�Ƿ���
        if (wifiManager.isWifiEnabled()) {
            // �Ѿ�������WiFi
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp1(ipAddress);
            return ip;
        } else {
            // δ����WiFi
            return "10.0.2.2";
        }
    }
    private static String intToIp0(int ipAddress) {
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                (ipAddress >> 24 & 0xFF);
    }
    private static String intToIp1(int ipAddress) {
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                1;
    }
    /**
     * ��ȡ����IPv4��ַ
     *
     * @return ����IPv4��ַ��null������������
     */
    private static String getIpAddress() {
        try {
            NetworkInterface networkInterface;
            InetAddress inetAddress;
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
            return null;
        } catch (SocketException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public static String getMacAddress(){
 /*��ȡmac��ַ��һ����Ҫע��ľ���android 6.0�汾������ע�ͷ����������ã������κ��ֻ����᷵��"02:00:00:00:00:00"���Ĭ�ϵ�mac��ַ������googel�ٷ�Ϊ�˼�ǿȨ�޹����������getSYstemService(Context.WIFI_SERVICE)���������mac��ַ��*/
        // String macAddress= "";
// WifiManager wifiManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
// WifiInfo wifiInfo = wifiManager.getConnectionInfo();
// macAddress = wifiInfo.getMacAddress();
// return macAddress;

        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }
    /**��ȡ�ֻ���IMEI����*/
    public static String getPhoneIMEI(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = android.os.Build.MODEL; // �ֻ��ͺ�
        String numer = mTm.getLine1Number(); // �ֻ����룬�еĿɵã��еĲ��ɵ�
      //  CommonUtils.LogD("phoneIMEI:" + imei);
        return imei;
    }
}
