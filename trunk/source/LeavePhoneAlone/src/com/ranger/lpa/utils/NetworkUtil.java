package com.ranger.lpa.utils;

import android.net.wifi.WifiConfiguration;

/**
 * Created by taoliang on 14-7-17.
 */
public class NetworkUtil {

    /**
     * Set WifiConfiguration
     */
    public static void setWifiConfigurationSettings(WifiConfiguration wc, String pass) {

        wc.preSharedKey = "\""
                + pass
                + "\"";
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

    }
}
