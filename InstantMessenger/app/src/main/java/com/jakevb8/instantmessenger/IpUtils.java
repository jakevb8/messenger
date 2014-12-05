package com.jakevb8.instantmessenger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

public class IpUtils {

    public static final String EthernetInterface = "eth0";
    public static final int MULTICAST_TTL = 255;

    public IpUtils() {
    }

    public static boolean connectedToEthernet(Context context) {
        NetworkInfo networkinfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(9);
        if (networkinfo == null) {
            return false;
        } else {
            return networkinfo.isConnectedOrConnecting();
        }
    }

    private DhcpInfo getDhcpInfo(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getDhcpInfo();
    }

    private WifiInfo getWifiInfo(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
    }

    public static boolean isWirelessDirect(Context context) {
        NetworkInfo networkinfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected() && networkinfo.getType() == 1) {
            DhcpInfo dhcpinfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getDhcpInfo();
            if (dhcpinfo != null && dhcpinfo.gateway == 0) {
                Log.d("IpUtils", "isWirelessDirect: probably wireless direct.");
                return true;
            }
        }
        return false;
    }

    public InetAddress getBroadcastAddress(Context context)
            throws UnknownHostException {
        if (connectedToEthernet(context)) {
            return null;
        }
        InetAddress inetaddress;
        NetworkInterface networkinterface = null;
        NetworkInterface networkinterface1 = null;
        try {
            networkinterface1 = NetworkInterface.getByName("eth0");
        } catch (SocketException socketexception) {
            networkinterface = null;
        }
        networkinterface = networkinterface1;

        inetaddress = null;
        if (networkinterface != null) {
            List list = networkinterface.getInterfaceAddresses();
            inetaddress = null;
            if (list != null) {
                boolean flag = list.isEmpty();
                inetaddress = null;
                if (!flag) {
                    Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        InetAddress inetaddress1 = ((InterfaceAddress) iterator.next()).getBroadcast();
                        if (inetaddress1 != null) {
                            inetaddress = inetaddress1;
                            return inetaddress;
                        }
                    }
                }
            }
        }

        WifiInfo wifiinfo;
        DhcpInfo dhcpinfo;
        wifiinfo = getWifiInfo(context);
        dhcpinfo = getDhcpInfo(context);
        inetaddress = null;
        if (wifiinfo == null) {
            return null;
        }
        inetaddress = null;
        if (dhcpinfo == null) {
            return null;
        }
        int i = wifiinfo.getIpAddress() & dhcpinfo.netmask | -1 ^ dhcpinfo.netmask;
        byte abyte0[] = new byte[4];
        abyte0[0] = (byte) (i & 0xff);
        abyte0[1] = (byte) (0xff & i >> 8);
        abyte0[2] = (byte) (0xff & i >> 16);
        abyte0[3] = (byte) (0xff & i >> 24);
        return InetAddress.getByAddress(abyte0);

    }

    public boolean isWifiStateEnabled(Context context) {
        int i = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getWifiState();
        return i == 2 || i == 3;
    }
}
