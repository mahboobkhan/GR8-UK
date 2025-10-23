package com.Gr8niteout.services;

import com.Gr8niteout.model.UserLoginResponse;
import com.Gr8niteout.model.UserSignUpResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserAuthService {
    
    @FormUrlEncoded
    @POST("webservice.php?request=user_login")
    Call<UserLoginResponse> userLogin(
            @Field("login_user_name") String email,
            @Field("login_password") String password
    );
    
    @FormUrlEncoded
    @POST("webservice.php?request=user_registration")
    Call<UserSignUpResponse> userSignUp(
            @Field("photo") String photo,
            @Field("app_ver") String appVer,
            @Field("s_token") String sToken,
            @Field("access_token") String accessToken,
            @Field("email") String email,
            @Field("country_code") String countryCode,
            @Field("d_type") String dType,
            @Field("country_name") String countryName,
            @Field("lname") String lastName,
            @Field("mobile") String mobile,
            @Field("p_token") String pToken,
            @Field("udid") String udid,
            @Field("fname") String firstName,
            @Field("birthdate") String birthdate,
            @Field("timezone") String timezone,
            @Field("gender") String gender,
            @Field("fb_id") String fbId,
            @Field("password") String password,
            @Field("username") String username
    );
}
