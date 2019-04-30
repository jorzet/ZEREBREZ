/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zerebrez.zerebrez.services.billing;

import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.zerebrez.zerebrez.ui.activities.PaywayActivityRefactor;

import java.util.List;


/**
 * BillingManager that handles all the interactions with Play Store
 * (via Billing library), maintain connection to it through BillingClient and cache
 * temporary states/data if needed.
 */
public class BillingManager implements PurchasesUpdatedListener {
    private static final String TAG = "BillingManager";

    private final BillingClient mBillingClient;
    private final PaywayActivityRefactor mActivity;

    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final int BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;

    public BillingManager(PaywayActivityRefactor activity) {
        mActivity = activity;
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
        startServiceConnectionIfNeeded(null);
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        Log.i(TAG, "onPurchasesUpdated() response: " + responseCode);
        switch(responseCode){
            case BILLING_RESPONSE_RESULT_OK:
                mActivity.onBillingResponseOk();
                break;
            case BILLING_RESPONSE_RESULT_USER_CANCELED:
                mActivity.onBillingResponseUserCanceled();
                break;
            case BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE:
                mActivity.onBillingResponseServiceUnavailable();
                break;
            case BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
                mActivity.onBillingResponseBillingUnavailable();
                break;
            case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
                mActivity.onBillingResponseItemUnavailable();
                break;
            case BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
                mActivity.onBillingResponseDeveloperError();
                break;
            case BILLING_RESPONSE_RESULT_ERROR:
                mActivity.onBillingResponseError();
                break;
            case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
                mActivity.onBillingResponseItemAlreadyOwned();
                break;
            case BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:
                mActivity.onBillingResponseItemNotOwned();
                break;
        }
    }

    /**
     * Trying to restart service connection if it's needed or just execute a request.
     * <p>Note: It's just a primitive example - it's up to you to implement a real retry-policy.</p>
     * @param executeOnSuccess This runnable will be executed once the connection to the Billing
     *                         service is restored.
     */
    private void startServiceConnectionIfNeeded(final Runnable executeOnSuccess) {
        if (mBillingClient.isReady()) {
            if (executeOnSuccess != null) {
                executeOnSuccess.run();
            }
        } else {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@BillingResponse int billingResponse) {
                    if (billingResponse == BillingResponse.OK) {
                        Log.i(TAG, "onBillingSetupFinished() response: " + billingResponse);
                        if (executeOnSuccess != null) {
                            executeOnSuccess.run();
                        }
                    } else {
                        Toast.makeText(mActivity,"Failed to connect GooglePlay",Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "onBillingSetupFinished() error code: " + billingResponse);
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Log.w(TAG, "onBillingServiceDisconnected()");
                }
            });
        }
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType final String itemType,
                                     final List<String> skuList, final SkuDetailsResponseListener listener) {
        // Specify a runnable to start when connection to Billing client is established
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList).setType(itemType).build();
                mBillingClient.querySkuDetailsAsync(skuDetailsParams,
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode,
                                    List<SkuDetails> skuDetailsList) {
                                listener.onSkuDetailsResponse(responseCode, skuDetailsList);
                            }
                        });
            }
        };

        // If Billing client was disconnected, we retry 1 time and if success, execute the query
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }


    public void startPurchaseFlow(final String skuId, final String billingType) {
        // Specify a runnable to start when connection to Billing client is established
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setType(billingType)
                        .setSku(skuId)
                        .build();
                mBillingClient.launchBillingFlow(mActivity, billingFlowParams);
            }
        };

        // If Billing client was disconnected, we retry 1 time and if success, execute the query
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }

    public void destroy() {
        mBillingClient.endConnection();
    }

    public interface OnBillingResponseListener{
        void onBillingResponseOk();
        void onBillingResponseUserCanceled();
        void onBillingResponseServiceUnavailable();
        void onBillingResponseBillingUnavailable();
        void onBillingResponseItemUnavailable();
        void onBillingResponseDeveloperError();
        void onBillingResponseError();
        void onBillingResponseItemAlreadyOwned();
        void onBillingResponseItemNotOwned();
    }
}