/*
 * Copyright [2018] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerebrez.zerebrez.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager

/**
 * This class give all network support
 *
 * Created by Jorge Zepeda Tinoco on 24/04/18.
 * jorzet.94@gmail.com
 */

class NetworkUtil {
    companion object {
        fun isConnected(context : Context) : Boolean {
            val connectivityManager =  context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.getActiveNetworkInfo()
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }

        fun isWifiDisable(context: Context) : Boolean {
            val wifiManager = context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiEnabled = wifiManager.isWifiEnabled
            return !wifiEnabled
        }

        fun isMobileNetworkDisable(context: Context): Boolean {
            val connectivityManager = context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val reason = mobileInfo.reason
            val mobileDisabled = (mobileInfo.state === NetworkInfo.State.DISCONNECTED) &&
                    (reason == null || reason == "specificDisabled")
            return mobileDisabled
        }

        fun isWifiConnected(context: Context) : Boolean {
            val connectivityManager = context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val wifiConnected = wifiInfo.getState() == NetworkInfo.State.CONNECTED
            return wifiConnected
        }

        fun isMobileNetworkConnected(context: Context) : Boolean {
            val connectivityManager = context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val mobileConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED
            return mobileConnected
        }

        fun setWifiEnable(context: Context, isWifiEnable : Boolean) {
            val wifiManager = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = isWifiEnable
        }

        fun setMobileNetworkEnable(context: Context, isMobileNetworkEnable : Boolean) {
            val conman = context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val conmanClass = Class.forName(conman.javaClass.getName())
            val iConnectivityManagerField = conmanClass.getDeclaredField("mService")
            iConnectivityManagerField.setAccessible(true)
            val iConnectivityManager = iConnectivityManagerField.get(conman);
            val iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.javaClass.getName());
            val setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean::class.javaPrimitiveType)
            setMobileDataEnabledMethod.setAccessible(true)

            setMobileDataEnabledMethod.invoke(iConnectivityManager, isMobileNetworkEnable)
        }
    }
}
