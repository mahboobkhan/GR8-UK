package com.Gr8niteout.utils;

import android.content.Context;
import android.util.Log;

import com.Gr8niteout.config.CommonUtilities;
import com.Gr8niteout.model.SignUpModel;
import com.Gr8niteout.model.UserLoginResponse;
import com.google.gson.Gson;

/**
 * Utility class for safely handling user data parsing and access throughout the application.
 * This class provides methods to safely parse and access user data from both SignUpModel (Facebook login)
 * and UserLoginResponse (email/password login) data structures.
 */
public class UserDataUtils {

    /**
     * Safely parse user data from preferences and return the appropriate model
     * @param context The context to get preferences
     * @return ParsedUserData object containing both models and helper methods
     */
    public static ParsedUserData parseUserData(Context context) {
        String userDataString = CommonUtilities.getPreference(context, CommonUtilities.pref_UserData);
        Log.d("UserDataUtils", "Parsing user data: " + (userDataString != null ? "present" : "null"));
        
        SignUpModel signUpModel = null;
        UserLoginResponse userLoginModel = null;
        
        if (userDataString != null && !userDataString.isEmpty()) {
            try {
                // Check if this is email/password login data by looking for ResponseInfo structure
                if (userDataString.contains("ResponseInfo") && userDataString.contains("end_first_name")) {
                    Log.d("UserDataUtils", "Detected email/password login data");
                    // This is email/password login data, use UserLoginResponse
                    Gson gson = new Gson();
                    userLoginModel = gson.fromJson(userDataString, UserLoginResponse.class);
                    Log.d("UserDataUtils", "UserLoginResponse parsing result: " + (userLoginModel != null ? "success" : "failed"));
                } else {
                    Log.d("UserDataUtils", "Detected Facebook login data");
                    // This is Facebook login data, use SignUpModel
                    signUpModel = new SignUpModel().SignUpModel(userDataString);
                    Log.d("UserDataUtils", "SignUpModel parsing result: " + (signUpModel != null ? "success" : "failed"));
                }
            } catch (Exception e) {
                Log.e("UserDataUtils", "Error parsing user data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.d("UserDataUtils", "User data string is null or empty");
        }
        
        return new ParsedUserData(signUpModel, userLoginModel);
    }
    
    /**
     * Get user ID from parsed data
     * @param parsedData The parsed user data
     * @return User ID or empty string if not available
     */
    public static String getUserId(ParsedUserData parsedData) {
        String userId = CommonUtilities.getPreference(parsedData.getContext(), CommonUtilities.pref_UserId);
        
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        
        // Try to extract from user data
        if (parsedData.getSignUpModel() != null && parsedData.getSignUpModel().response != null && 
            parsedData.getSignUpModel().response.user_data != null) {
            return parsedData.getSignUpModel().response.user_data.user_id != null ? 
                   parsedData.getSignUpModel().response.user_data.user_id : "";
        } else if (parsedData.getUserLoginModel() != null && parsedData.getUserLoginModel().response != null && 
                   parsedData.getUserLoginModel().response.responseInfo != null && 
                   parsedData.getUserLoginModel().response.responseInfo.data != null) {
            return parsedData.getUserLoginModel().response.responseInfo.data.user_id != null ? 
                   parsedData.getUserLoginModel().response.responseInfo.data.user_id : "";
        }
        
        return "";
    }
    
    /**
     * Get user first name from parsed data
     * @param parsedData The parsed user data
     * @return First name or empty string if not available
     */
    public static String getFirstName(ParsedUserData parsedData) {
        if (parsedData.getSignUpModel() != null && parsedData.getSignUpModel().response != null && 
            parsedData.getSignUpModel().response.user_data != null) {
            return parsedData.getSignUpModel().response.user_data.fname != null ? 
                   parsedData.getSignUpModel().response.user_data.fname : "";
        } else if (parsedData.getUserLoginModel() != null && parsedData.getUserLoginModel().response != null && 
                   parsedData.getUserLoginModel().response.responseInfo != null && 
                   parsedData.getUserLoginModel().response.responseInfo.data != null) {
            return parsedData.getUserLoginModel().response.responseInfo.data.end_first_name != null ? 
                   parsedData.getUserLoginModel().response.responseInfo.data.end_first_name : "";
        }
        
        return "";
    }
    
    /**
     * Get user last name from parsed data
     * @param parsedData The parsed user data
     * @return Last name or empty string if not available
     */
    public static String getLastName(ParsedUserData parsedData) {
        if (parsedData.getSignUpModel() != null && parsedData.getSignUpModel().response != null && 
            parsedData.getSignUpModel().response.user_data != null) {
            return parsedData.getSignUpModel().response.user_data.lname != null ? 
                   parsedData.getSignUpModel().response.user_data.lname : "";
        } else if (parsedData.getUserLoginModel() != null && parsedData.getUserLoginModel().response != null && 
                   parsedData.getUserLoginModel().response.responseInfo != null && 
                   parsedData.getUserLoginModel().response.responseInfo.data != null) {
            return parsedData.getUserLoginModel().response.responseInfo.data.end_last_name != null ? 
                   parsedData.getUserLoginModel().response.responseInfo.data.end_last_name : "";
        }
        
        return "";
    }
    
    /**
     * Get user email from parsed data
     * @param parsedData The parsed user data
     * @return Email or empty string if not available
     */
    public static String getEmail(ParsedUserData parsedData) {
        if (parsedData.getSignUpModel() != null && parsedData.getSignUpModel().response != null && 
            parsedData.getSignUpModel().response.user_data != null) {
            return parsedData.getSignUpModel().response.user_data.email != null ? 
                   parsedData.getSignUpModel().response.user_data.email : "";
        } else if (parsedData.getUserLoginModel() != null && parsedData.getUserLoginModel().response != null && 
                   parsedData.getUserLoginModel().response.responseInfo != null && 
                   parsedData.getUserLoginModel().response.responseInfo.data != null) {
            return parsedData.getUserLoginModel().response.responseInfo.data.end_email != null ? 
                   parsedData.getUserLoginModel().response.responseInfo.data.end_email : "";
        }
        
        return "";
    }
    
    /**
     * Get user profile photo from parsed data
     * @param parsedData The parsed user data
     * @return Profile photo path or empty string if not available
     */
    public static String getProfilePhoto(ParsedUserData parsedData) {
        if (parsedData.getSignUpModel() != null && parsedData.getSignUpModel().response != null && 
            parsedData.getSignUpModel().response.user_data != null) {
            return parsedData.getSignUpModel().response.user_data.getPhoto() != null ? 
                   parsedData.getSignUpModel().response.user_data.getPhoto() : "";
        } else if (parsedData.getUserLoginModel() != null && parsedData.getUserLoginModel().response != null && 
                   parsedData.getUserLoginModel().response.responseInfo != null && 
                   parsedData.getUserLoginModel().response.responseInfo.data != null) {
            return parsedData.getUserLoginModel().response.responseInfo.data.end_profile_pic != null ? 
                   parsedData.getUserLoginModel().response.responseInfo.data.end_profile_pic : "";
        }
        
        return "";
    }
    
    /**
     * Get user mobile number from parsed data
     * @param parsedData The parsed user data
     * @return Mobile number or empty string if not available
     */
    public static String getMobileNumber(ParsedUserData parsedData) {
        if (parsedData.getSignUpModel() != null && parsedData.getSignUpModel().response != null && 
            parsedData.getSignUpModel().response.user_data != null) {
            return parsedData.getSignUpModel().response.user_data.getMobile() != null ? 
                   parsedData.getSignUpModel().response.user_data.getMobile() : "";
        } else if (parsedData.getUserLoginModel() != null && parsedData.getUserLoginModel().response != null && 
                   parsedData.getUserLoginModel().response.responseInfo != null && 
                   parsedData.getUserLoginModel().response.responseInfo.data != null) {
            return parsedData.getUserLoginModel().response.responseInfo.data.mobile_no != null ? 
                   parsedData.getUserLoginModel().response.responseInfo.data.mobile_no : "";
        }
        
        return "";
    }
    
    /**
     * Get user country code from parsed data
     * @param parsedData The parsed user data
     * @return Country code or empty string if not available
     */
    public static String getCountryCode(ParsedUserData parsedData) {
        if (parsedData.getSignUpModel() != null && parsedData.getSignUpModel().response != null && 
            parsedData.getSignUpModel().response.user_data != null) {
            return parsedData.getSignUpModel().response.user_data.getCc_code() != null ? 
                   parsedData.getSignUpModel().response.user_data.getCc_code() : "";
        } else if (parsedData.getUserLoginModel() != null && parsedData.getUserLoginModel().response != null && 
                   parsedData.getUserLoginModel().response.responseInfo != null && 
                   parsedData.getUserLoginModel().response.responseInfo.data != null) {
         //   return parsedData.getUserLoginModel().response.responseInfo.data.cc_code != null ? parsedData.getUserLoginModel().response.responseInfo.data.cc_code : "";
        }
        
        return "";
    }
    
    /**
     * Check if user is logged in
     * @param context The context to get preferences
     * @return True if user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn(Context context) {
        String userId = CommonUtilities.getPreference(context, CommonUtilities.pref_UserId);
        return userId != null && !userId.isEmpty();
    }
    
    /**
     * Container class for parsed user data
     */
    public static class ParsedUserData {
        private SignUpModel signUpModel;
        private UserLoginResponse userLoginModel;
        private Context context;
        
        public ParsedUserData(SignUpModel signUpModel, UserLoginResponse userLoginModel) {
            this.signUpModel = signUpModel;
            this.userLoginModel = userLoginModel;
        }
        
        public SignUpModel getSignUpModel() {
            return signUpModel;
        }
        
        public UserLoginResponse getUserLoginModel() {
            return userLoginModel;
        }
        
        public Context getContext() {
            return context;
        }
        
        public void setContext(Context context) {
            this.context = context;
        }
        
        public boolean hasSignUpModel() {
            return signUpModel != null && signUpModel.response != null && signUpModel.response.user_data != null;
        }
        
        public boolean hasUserLoginModel() {
            return userLoginModel != null && userLoginModel.response != null && 
                   userLoginModel.response.responseInfo != null && userLoginModel.response.responseInfo.data != null;
        }
    }
}
