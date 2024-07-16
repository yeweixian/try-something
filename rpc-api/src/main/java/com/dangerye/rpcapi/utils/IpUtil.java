package com.dangerye.rpcapi.utils;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IpUtil {

    public static String getLocalIp() {
        return Optional.ofNullable(getLocalIpList())
                .filter(item -> item.size() > 0)
                .map(item -> item.get(0))
                .orElse(null);
    }

    public static List<String> getLocalIpList() {
        try {
            List<String> list = new ArrayList<>();
            Optional.ofNullable(NetworkInterface.getNetworkInterfaces())
                    .ifPresent(networkInterfaceEnumeration -> {
                        while (networkInterfaceEnumeration.hasMoreElements()) {
                            Optional.ofNullable(networkInterfaceEnumeration.nextElement())
                                    .map(NetworkInterface::getInetAddresses)
                                    .ifPresent(inetAddressEnumeration -> {
                                        while (inetAddressEnumeration.hasMoreElements()) {
                                            Optional.ofNullable(inetAddressEnumeration.nextElement())
                                                    .ifPresent(inetAddress -> {
                                                        if (inetAddress instanceof Inet4Address) {
                                                            String ip = inetAddress.getHostAddress();
                                                            list.add(ip);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
