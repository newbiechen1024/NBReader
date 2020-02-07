package com.newbiechen.nbreader.uilts

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import java.net.NetworkInterface
import java.net.SocketException

/**
 *  author : newbiechen
 *  date : 2019-08-10 17:17
 *  description :
 */

object NetworkUtil {

    private const val TAG = "NetworkUtils"
    /**
     * Same to [ConnectivityManager.TYPE_WIMAX] (API 8)
     */
    private const val CM_TYPE_WIMAX = 6
    /**
     * Same to [ConnectivityManager.TYPE_ETHERNET] (API 13)
     */
    private const val CM_TYPE_ETHERNET = 9
    /**
     * Same to [ConnectivityManager.TYPE_MOBILE_MMS] (API 8)
     */
    private const val CM_TYPE_MOBILE_MMS = 2
    /**
     * Same to [ConnectivityManager.TYPE_BLUETOOTH] (API 8)
     */
    private const val CM_TYPE_BLUETOOTH = 7

    /**
     * Same to [TelephonyManager.NETWORK_TYPE_EVDO_B] (API 9) 5 Mbps
     */
    private const val TM_NETWORK_TYPE_EVDO_B = 12
    /**
     * Same to [TelephonyManager.NETWORK_TYPE_LTE] (API 11) 10+ Mbps
     */
    private const val TM_NETWORK_TYPE_LTE = 13
    /**
     * Same to [TelephonyManager.NETWORK_TYPE_EHRPD] (API 11) 1~2 Mbps
     */
    private const val TM_NETWORK_TYPE_EHRPD = 14
    /**
     * Same to [TelephonyManager.NETWORK_TYPE_HSPAP] (API 13) 10~20 Mbps
     */
    private const val TM_NETWORK_TYPE_HSPAP = 15

    const val NET_TYPE_NONE = -1
    // Don't touch! The following network types are defined by Server.
    const val NET_TYPE_WIFI = 1
    const val NET_TYPE_2G = 2
    const val NET_TYPE_3G = 3
    const val NET_TYPE_MOBILE = 4
    const val NET_TYPE_4G = 5

    private var sCM: ConnectivityManager? = null

    private fun getConnectivityManager(cxt: Context): ConnectivityManager? {
        if (sCM == null) {
            sCM = cxt.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
        }
        return sCM
    }

    /**
     * @param cxt
     * @return One of values [.NET_TYPE_WIFI], [.NET_TYPE_2G],
     * [.NET_TYPE_3G] or [.NET_TYPE_NONE]
     */
    fun getNetworkType(cxt: Context): Int {
        val connMgr = getConnectivityManager(cxt) ?: return NET_TYPE_NONE
        val netInfo = connMgr.activeNetworkInfo
        if (netInfo != null) {
            val type = netInfo.type
            val subType = netInfo.subtype
            LogHelper.i(TAG, "network type = $type : $subType")
            if (type == ConnectivityManager.TYPE_WIFI
                || type == CM_TYPE_WIMAX
                || type == CM_TYPE_ETHERNET
            ) {
                return NET_TYPE_WIFI
            } else if (type == ConnectivityManager.TYPE_MOBILE || type == CM_TYPE_BLUETOOTH && subType > 0) {
                /*
                 * this patch for fix in some devices type when apn connected, report type is
                 * TYPE_BLUETOOTH and has subtype.  tested on CoolPad 7260+
                 */
                if (subType == TelephonyManager.NETWORK_TYPE_UMTS
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_HSUPA
                    || subType == TelephonyManager.NETWORK_TYPE_HSPA
                    || subType == TM_NETWORK_TYPE_EVDO_B
                    || subType == TM_NETWORK_TYPE_EHRPD
                    || subType == TM_NETWORK_TYPE_HSPAP
                ) {
                    return NET_TYPE_3G
                } else if (subType == TM_NETWORK_TYPE_LTE) {
                    return NET_TYPE_4G
                }
                return NET_TYPE_2G // Take other data types as 2G
            } else if (type == CM_TYPE_MOBILE_MMS || type == CM_TYPE_BLUETOOTH) {
                // when mms and bluetooth, don't recognize as mobile
                return NET_TYPE_NONE
            }
            return NET_TYPE_2G // Take unknown networks as 2G
        }
        return NET_TYPE_NONE
    }

    /**
     * @param cxt
     * @return One of the values [.NET_TYPE_NONE] or [.NET_TYPE_WIFI] or [.NET_TYPE_MOBILE]
     */
    fun getSimpleNetworkType(cxt: Context): Int {
        val connMgr = getConnectivityManager(cxt) ?: return NET_TYPE_NONE
        val netInfo = connMgr.activeNetworkInfo
        if (netInfo != null) {
            val type = netInfo.type
            return if (type == ConnectivityManager.TYPE_WIFI || type == CM_TYPE_WIMAX
                || type == CM_TYPE_ETHERNET
            ) {
                NET_TYPE_WIFI
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                NET_TYPE_MOBILE
            } else if (type == CM_TYPE_MOBILE_MMS || type == CM_TYPE_BLUETOOTH) {
                NET_TYPE_NONE
            } else {
                // Take unknown networks as mobile network
                NET_TYPE_MOBILE
            }
        }
        return NET_TYPE_NONE
    }

    /**
     * True only when network type equals wifi and network available
     */
    fun isWifiAvailable(ctx: Context): Boolean {
        return getSimpleNetworkType(ctx) == NET_TYPE_WIFI && isNetworkAvaialble(ctx)
    }

    /**
     * isNetworkAvaialble
     *
     * @param mustConnected Check if there is an active network connection
     */
    @JvmOverloads
    fun isNetworkAvaialble(ctx: Context, mustConnected: Boolean = true): Boolean {
        val connMgr = getConnectivityManager(ctx) ?: return false
        val network = connMgr.activeNetworkInfo

        return if (mustConnected) {
            network != null && network.isConnected && network.isAvailable
        } else {
            network != null && network.isAvailable
        }
    }

    /**
     * Get the IP address of the device.
     *
     * @return null may be returned
     */
    fun getIpAddress(ctx: Context): String? {
        val cm = getConnectivityManager(ctx) ?: return null
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnected) {
            LogHelper.i(TAG, "Active network found")
            try {
                val netIter = NetworkInterface.getNetworkInterfaces()
                while (netIter.hasMoreElements()) {
                    val netInterface = netIter.nextElement()
                    val inetAddrIter = netInterface.inetAddresses
                    while (inetAddrIter.hasMoreElements()) {
                        val inetAddr = inetAddrIter.nextElement()
                        val ip = inetAddr.hostAddress
                        if (!inetAddr.isLoopbackAddress && !TextUtils.isEmpty(ip)) {
                            LogHelper.i(
                                TAG, "Host title: " + inetAddr.hostName
                                        + ", IP: " + ip
                            )
                            return ip
                        }
                    }
                }
            } catch (e: SocketException) {
                LogHelper.w(TAG, "Failed to get network IP with exception: $e")
            }

        }
        LogHelper.i(TAG, "Failed to get IP address")
        return null
    }

    private fun isNetworkMobile(ctx: Context): Boolean {
        val cm = getConnectivityManager(ctx) ?: return false
        val networkInfo = cm.activeNetworkInfo
        return if (networkInfo != null) {
            networkInfo.type == ConnectivityManager.TYPE_MOBILE
        } else false
    }
}
/**
 * Check if there is an active network connection
 */