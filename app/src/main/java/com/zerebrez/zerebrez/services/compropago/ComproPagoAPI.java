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

import com.zerebrez.zerebrez.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Jesus Campos on 05/09/18.
 * jcampos.jc38@gmail.com
 */

public interface ComproPagoAPI {

    //Consult the providers list
    @Headers({
            "Accept: application/compropago",
            "Content-type: application/json"
    })
    @GET("/v1/providers")
    Call<List<Provider>> getProviders(@Header("Authorization") String credentials);


    //Create a new order
    @Headers({
            "Accept: application/compropago",
            "Content-type: application/json"
    })
    @POST("/v1/charges")
    Call<OrderResponse> generateOrder(@Body OrderRequest orderRequest, @Header("Authorization") String credentials);

    //Consult an existing charge
    @Headers({
            "Accept: application/compropago",
            "Content-type: application/json"
    })
    @GET("/v1/charges/{payment_id}")
    Call<ChargeResponse> verifyCharge(@Path("payment_id") String paymentId, @Header("Authorization") String credentials);

}
