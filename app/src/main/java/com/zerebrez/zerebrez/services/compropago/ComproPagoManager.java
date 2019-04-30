/*
 * Copyright [2019] [Jorge Zepeda Tinoco]
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

package com.zerebrez.zerebrez.services.compropago;

import android.support.annotation.NonNull;

import com.zerebrez.zerebrez.models.*;
import java.util.List;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jesus Campos on 05/09/18.
 * jcampos.jc38@gmail.com
 */

public class ComproPagoManager {

    /*
     * Tags
     */
    private static final String BASE_URL = "https://api.compropago.com";
    private static final String PK_TEST = "pk_test_496575160fe502e1aa";
    private static final String SK_TEST = "sk_test_7be79c8752725ac584";
    private static final String PK_LIVE = "pk_live_518507516122571bf3";
    private static final String SK_LIVE = "sk_live_5be40752805f3573e6";

    /*
     * Manager
     */
    private final ComproPagoAPI comproPagoAPI;

    /**
     * {@link ComproPagoManager} constructor
     */
    public ComproPagoManager() {
        comproPagoAPI = RetrofitClientInstance
                .getRetrofitInstance(BASE_URL)
                .create(ComproPagoAPI.class);
    }

    /**
     * The method return on {@link OnListProvidersListener} the provider list
     *
     * @param onListProvidersListener listener {@link OnListProvidersListener}
     */
    public void listProviders(final OnListProvidersListener onListProvidersListener) {
        Call<List<Provider>> call =
                comproPagoAPI.getProviders(okhttp3.Credentials.basic(PK_LIVE,null));

        call.enqueue(new Callback<List<Provider>>() {
            @Override
            public void onResponse(@NonNull Call<List<Provider>> call,
                                   @NonNull Response<List<Provider>> response) {
                onListProvidersListener.onListProvidersResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<List<Provider>> call, @NonNull Throwable t) {
                onListProvidersListener.onListProvidersFailure(t);
            }
        });
    }

    /**
     * Create a request to generate comproPago order
     *
     * @param course course Id
     * @param customerName course text name
     * @param customerEmail user email
     * @param paymentType provider
     * @param orderPrice price
     * @param onGenerateOrderListener success or error listener {@link OnGenerateOrderListener}
     */
    @SuppressWarnings("SpellCheckingInspection")
    public void generateOrder(Course course, String customerName, String customerEmail,
                              String paymentType, Float orderPrice,
                              final OnGenerateOrderListener onGenerateOrderListener) {

        OrderRequest orderRequest = new OrderRequest(course.getId(),
                0.0f,
                course.getComproPagoDescription(),
                "",
                "",
                "",
                "",
                "MXN");
        orderRequest.setCustomer_name(customerName);
        orderRequest.setCustomer_email(customerEmail);
        orderRequest.setPayment_type(paymentType);
        orderRequest.setOrder_price(orderPrice);
        Call<OrderResponse> call =
                comproPagoAPI.generateOrder(orderRequest,
                                            okhttp3.Credentials.basic(PK_LIVE,
                                            null));

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderResponse> call,
                                   @NonNull Response<OrderResponse> response) {
                onGenerateOrderListener.onGenerateOrderResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                onGenerateOrderListener.onGenerateOrderFailure(t);
            }
        });
    }

    /**
     * this method verify updates
     *
     * @param paymentId payment id got it from comproPago response
     * @param onVerifyChargeListener listener {@link OnVerifyChargeListener}
     */
    @SuppressWarnings("SpellCheckingInspection")
    public void verifyCharge(String paymentId,
                             final OnVerifyChargeListener onVerifyChargeListener) {

        Call<ChargeResponse> call =
                comproPagoAPI.verifyCharge(paymentId, Credentials.basic(SK_LIVE, PK_LIVE));

        call.enqueue(new Callback<ChargeResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChargeResponse> call,
                                   @NonNull Response<ChargeResponse> response) {
                onVerifyChargeListener.onVerifyChargeResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<ChargeResponse> call, @NonNull Throwable t) {
                onVerifyChargeListener.onVerifyChargeFailure(t);
            }
        });
    }

    public interface OnGenerateOrderListener {
        void onGenerateOrderResponse(Response<OrderResponse> response);
        void onGenerateOrderFailure(Throwable throwable);
    }

    public interface OnVerifyChargeListener {
        void onVerifyChargeResponse(Response<ChargeResponse> response);
        void onVerifyChargeFailure(Throwable throwable);
    }

    public interface OnListProvidersListener {
        void onListProvidersResponse(Response<List<Provider>> response);
        void onListProvidersFailure(Throwable throwable);
    }

}
