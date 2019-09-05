package org.noear.solonjt.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class LocalUtil {
    public static String getLocalIp() {
        String host = null;

        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            Enumeration ee = null;

            while(en.hasMoreElements()) {
                ee = ((NetworkInterface)en.nextElement()).getInetAddresses();

                while(ee.hasMoreElements()) {
                    host = ((InetAddress)ee.nextElement()).getHostAddress();
                    if (!TextUtils.isEmpty(host) && (host.startsWith("192.") || host.startsWith("172.") || host.startsWith("10."))) {
                        return host;
                    }
                }
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return host;
    }

    public static String getLocalAddress(int port) {
        String host = null;

        try {
            host = LocalUtil.getLocalIp();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return port > 0 ? host + ":" + port : host;
    }
}
