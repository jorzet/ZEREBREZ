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
    Call<OrderResponse> GenerateOrder(@Body OrderRequest orderRequest, @Header("Authorization") String credentials);

    //Consult an existing charge
    @Headers({
            "Accept: application/compropago",
            "Content-type: application/json"
    })
    @GET("/v1/charges/{payment_id}")
    Call<ChargeResponse> VerifyCharge(@Path("payment_id") String paymentId, @Header("Authorization") String credentials);

}
