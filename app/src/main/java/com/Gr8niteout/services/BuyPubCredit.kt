package com.Gr8niteout.services

import com.Gr8niteout.model.BuyPubCreditResponseModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BuyPubCredit {
    @FormUrlEncoded
    @POST("StripeController/get_stripe_key_mobile")
    fun buyPubCredit(
            @Field("pub_id") pubId : String?,
            @Field("credit_amount") creditAmount : String?,
            @Field("userfile") userFile : String?,
            @Field("slugm") slugm : String?,
            @Field("sender_email") senderEmail : String?,
            @Field("first_name") senderFirstName : String?,
            @Field("surname") senderSurname : String?,
            @Field("country_num") senderCountryNum : String?,
            @Field("sender_mobile") senderMobile : String?,
            @Field("recipient_name") recipientName : String?,
            @Field("recipient_email") recipientEmail : String?,
            @Field("recipient_num") recipientNumber : String?,
            @Field("recipient_code") recipientCode : String?,
            @Field("comment") comment : String?
    ) : Call<BuyPubCreditResponseModel>
}