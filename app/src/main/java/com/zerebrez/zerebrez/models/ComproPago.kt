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

package com.zerebrez.zerebrez.models

import java.io.Serializable

/**
 * Created by Jesus Campos on 05/09/18.
 * jcampos.jc38@gmail.com
 */

data class Provider(val internal_name: String,
    val availability: String,
    val name: String,
    val rank: Int,
    val transaction_limit: Int,
    val commission: Int,
    val is_active: Boolean,
    val store_image: String,
    val image_small: String,
    val image_medium: String,
    val image_large: String,
    val type: String): Serializable

data class OrderRequest(
    val order_id: String,
    var order_price: Float,
    val order_name: String,
    val image_url: String,
    var customer_name: String,
    var customer_email: String,
    var payment_type: String,
    val currency: String): Serializable

data class OrderResponse(
    val id: String,
    val short_id: String,
    val type: String,
    val Object: String,
    val created_at: Long,
    val accepted_at: Long,
    val expires_in: Long,
    val paid: Boolean,
    val amount: Float,
    val livemode: Boolean,
    val currency: String,
    val refunded: Boolean,
    val fee: Float,
    val fee_details: FeeDetails,
    val order_info: OrderInfo,
    val customer: Customer,
    val instructions: Instructions,
    val api_version: String): Serializable

data class ChargeResponse(
        val id: String,
        val short_id: String,
        val type: String,
        val Object: String,
        val created_at: Long,
        val accepted_at: Long,
        val expires_in: Long,
        val paid: Boolean,
        val amount: Float,
        val livemode: Boolean,
        val currency: String,
        val refunded: Boolean,
        val fee: Float,
        val fee_details: FeeDetails,
        val order_info: OrderInfo2,
        val customer: Customer,
        val api_version: String): Serializable

data class FeeDetails(
    var amount: Float,
    var currency: String,
    var type: String,
    var application: String,
    var amount_refunded: Float,
    var tax: Float): Serializable

data class OrderInfo(
    val order_id: String,
    val order_price: Float,
    val order_name: String,
    val payment_method: String,
    val store: String,
    val country: String,
    val image_url: String,
    val success_url: String,
    val failed_url: String,
    val exchange: Exchange): Serializable

data class OrderInfo2(
        val order_id: String,
        val order_price: Float,
        val order_name: String,
        val payment_method: String,
        val store: String,
        val country: String,
        val image_url: String,
        val success_url: String,
        val failed_url: String,
        val exchange: Exchange2): Serializable

data class Customer(
    val customer_name: String,
    val customer_email: String,
    val customer_phone: String): Serializable

data class Instructions(
    val description: String,
    val step_1: String,
    val step_2: String,
    val step_3: String,
    val note_extra_comition: String,
    val note_expiration_date: String,
    val note_confirmation: String,
    val details: Details): Serializable

data class Exchange(
    val rate: Double,
    val request: String,
    val origin_amount: Float,
    val final_amount: Float,
    val origin_currency: String,
    val final_currency: String,
    val exchange_id: String
): Serializable

data class Exchange2(
        val rate: Rate,
        val request: String,
        val origin_amount: Float,
        val final_amount: Float,
        val origin_currency: String,
        val final_currency: String,
        val exchange_id: String
): Serializable

data class Details(
    val payment_amount: String,
    val payment_store: String,
    val amount: String,
    val store: String,
    val bank_account_number: String,
    val company_reference_name: String,
    val company_reference_number: String,
    val company_bank_number: String,
    val order_reference_number: String,
    val bank_account_holder_name: String,
    val bank_reference: String,
    val bank_name: String): Serializable

data class Rate(val pair: String, val rate: Double) : Serializable