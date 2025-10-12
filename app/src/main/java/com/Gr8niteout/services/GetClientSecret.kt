package com.Gr8niteout.services

import com.Gr8niteout.model.ClientSecretModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface GetClientSecret {
    @FormUrlEncoded
    @POST("v1/payment_intents")
    fun getClientSecret(
        @Header("Authorization") auth: String?,
        @Field("amount") amount : String?,
        @Field("currency") currency : String?,
        @Field("payment_method_types[]") paymentMethod : String?,
    ) : Call<ClientSecretModel>
}