package com.Gr8niteout.buycredits

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity
import com.Gr8niteout.BuildConfig
import com.Gr8niteout.R
import com.Gr8niteout.config.CommonUtilities
import com.Gr8niteout.config.ServerAccess
import com.Gr8niteout.config.ServerAccess.VolleyCallback
import com.Gr8niteout.model.StripeUserModel
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.StripeIntent
// Removed deprecated kotlinx.android.synthetic import - using findViewById instead
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.ref.WeakReference
import androidx.core.net.toUri

class PayActivity : AppCompatActivity() {

    private lateinit var paymentIntentClientSecret: String
    private lateinit var stripe: Stripe
    private lateinit var payBtn: Button
    private lateinit var pubCreditAmount: TextView
    private lateinit var totalAmount: TextView
    private lateinit var bookingFeeAmount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        
        // Initialize views with findViewById
        payBtn = findViewById(R.id.payBtn)
        pubCreditAmount = findViewById(R.id.pub_credit_amount)
        totalAmount = findViewById(R.id.total_amount)
        bookingFeeAmount = findViewById(R.id.booking_fee_amount)
        
        initialiseViews()
        setUpToolbar()
        setUpStripe() // Enable Stripe setup for testing



//        val paramsTemp: MutableMap<String, String> = HashMap()
        Log.d("paramsTemp------>>", intent.getStringExtra(CommonUtilities.key_rec_photo).toString());
        Log.d("paramsTemp------>>", intent.getStringExtra(CommonUtilities.key_rec_video).toString());

    }

    private fun initialiseViews(){
        val pubCreditAmountValue = intent.getStringExtra(CommonUtilities.key_amount) ?: "0.00"
        val totalAmountValue = intent.getStringExtra(CommonUtilities.key_amount_booking_fee) ?: "0.00"
        pubCreditAmount.text = pubCreditAmountValue
        totalAmount.text = totalAmountValue
        bookingFeeAmount.text = "1.00"
    }
    private fun setUpStripe(){
        paymentIntentClientSecret = intent.getStringExtra(CommonUtilities.clientSecretIntentKey) ?: ""
        Log.d("PayActivityTrack", "paymentIntentClientSecret : $paymentIntentClientSecret")
        Log.d("PayActivityTrack", "Using Stripe Test Key: ${BuildConfig.STRIPE_PUBLISHABLE_KEY}")
        stripe = Stripe(applicationContext, BuildConfig.STRIPE_PUBLISHABLE_KEY)
        
        // Log test mode configuration
        if (BuildConfig.STRIPE_PUBLISHABLE_KEY.startsWith("pk_test_")) {
            Log.d("PayActivityTrack", "✅ Stripe Test Mode Enabled - Safe for Pakistan Testing")
        } else {
            Log.w("PayActivityTrack", "⚠️ Warning: Not using test keys - make sure this is intended!")
        }
    }

    private fun setUpToolbar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.contentInsetStartWithNavigation = 0
        supportActionBar?.title = "Checkout"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun convertImgToBase64(bmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val byteFormat = stream.toByteArray()
        // get the base 64 string
        val imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP)
        return imgString
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }
    private fun convertVideoToBase64(videoPath: String): String {
        val file = File(videoPath)
        val bytes = file.readBytes()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }


    fun pay(view: View){
        val paramsTemp: MutableMap<String, String> = HashMap()
        paramsTemp["pub_id"] = intent.getStringExtra(CommonUtilities.key_pub_id).toString()
        paramsTemp["user_id"] = intent.getStringExtra(CommonUtilities.key_user_id).toString()
        paramsTemp["user_name"] = intent.getStringExtra("user_name").toString()
        paramsTemp["user_email"] = intent.getStringExtra("user_email").toString()
        paramsTemp["credit"] = intent.getStringExtra(CommonUtilities.key_amount_booking_fee).toString()
        paramsTemp["recipient_name"] = intent.getStringExtra("recipient_name").toString()
        paramsTemp["recipient_email"] = intent.getStringExtra("recipient_email").toString()
        paramsTemp["currency"] = intent.getStringExtra("currency").toString()
        paramsTemp["comment"] = intent.getStringExtra("comment").toString()

        val pickedImageUri: Uri? = intent.getStringExtra(CommonUtilities.key_rec_photo)?.let { it.toUri() }
        val pickedVideoUri: Uri? = intent.getStringExtra(CommonUtilities.key_rec_video)?.let { it.toUri() }

        val imageData = intent.getStringExtra(CommonUtilities.key_rec_photo).toString()

        if (imageData.isNotEmpty()) {

            try {
                val bitmap: Bitmap = if (pickedImageUri?.scheme == "content") {
                    // Use contentResolver if it's a content:// URI
                    MediaStore.Images.Media.getBitmap(contentResolver, pickedImageUri)
                } else {
                    // Decode the file directly if it's a file:// URI
                    BitmapFactory.decodeFile(pickedImageUri?.path)
                }

                // Compress the bitmap to reduce size
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)

                // Convert to Base64 and add to params
                paramsTemp["image_path"] = convertImgToBase64(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception (e.g., show a toast or log the error)
                Log.e("ImageProcessing", "Error processing image: ${e.localizedMessage}")
            }
//            try {
//                // Get the bitmap from the URI
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, pickedImageUri)
//
//                // Compress the bitmap to reduce size
//                val stream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
//
//                // Convert to Base64 and add to params
//                paramsTemp["image_path"] = convertImgToBase64(bitmap)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                // Handle the exception (e.g., show a toast or log the error)
//                Log.e("ImageProcessing", "Error processing image: ${e.localizedMessage}")
//            }
        } else {
            Log.e("ImageProcessing", "Picked image URI is null")
            // Handle the null case (e.g., show an error message)
        }

        val videoData = intent.getStringExtra(CommonUtilities.key_rec_video).toString()

        if (videoData.isNotEmpty()) {
            try {
                // Get the actual file path from the URI
                val videoPath = pickedVideoUri?.let { getRealPathFromURI(it) }

                if (videoPath != null) {
                    // Convert video file to Base64 (if needed)
                    val base64Video = convertVideoToBase64(videoPath)

                    // Add the video path or Base64 string to the params
                    paramsTemp["video_path"] = base64Video // Add the file path
//                    paramsTemp["video_base64"] = base64Video // Add Base64 string (optional)
                } else {
                    Log.e("VideoProcessing", "Could not get the real path from URI")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception
                Log.e("VideoProcessing", "Error processing video: ${e.localizedMessage}")
            }
        } else {
            Log.e("VideoProcessing", "Picked video URI is null")
            // Handle the null case
        }


        Log.e("paramsTemp------>>>", paramsTemp.toString())

        ServerAccess.getResponse(this,
            CommonUtilities.key_stripe_user,
            paramsTemp,
            true,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    Log.d("PayActivityTrack", "Stripe API Response: $result")
                    val stripeUserModel:StripeUserModel = StripeUserModel().StripeUserModel(result)
                    if (stripeUserModel.code == CommonUtilities.key_success_code) {
                        val intent = Intent(
                            this@PayActivity,
                            StripeWebViewActivity::class.java
                        )
                        intent.putExtra("ViewUrl", stripeUserModel.data.url)
                        intent.putExtra("pub_id", intent.getStringExtra(CommonUtilities.key_pub_id).toString())
                        intent.putExtra(CommonUtilities.key_user_id, intent.getStringExtra(CommonUtilities.key_user_id).toString())
                        intent.putExtra("successUrl", stripeUserModel.data.success_url)
                        intent.putExtra("failureUrl", stripeUserModel.data.cancel_url)
                        intent.putExtra("isFromUserLogin", 1)
                        startActivity(intent)
                    } else {
                        // Show detailed error information in debug mode
                        showDetailedError(stripeUserModel)
                    }
                }

                override fun onError(error: String) {
                    Log.e("PayActivityTrack", "Network Error: $error")
                    CommonUtilities.ShowToast(this@PayActivity, "Network Error: $error")
                }
            })
    }

    private fun showDetailedError(stripeUserModel: StripeUserModel) {
        val errorMessage = StringBuilder()
        
        // Basic error info
        errorMessage.append("Payment Failed\n")
        errorMessage.append("Status: ${stripeUserModel.status}\n")
        errorMessage.append("Code: ${stripeUserModel.code}\n")
        errorMessage.append("Message: ${stripeUserModel.msg}\n")
        
        // Detailed error info if available
        if (stripeUserModel.data?.error != null) {
            val error = stripeUserModel.data.error
            errorMessage.append("\n--- Detailed Error ---\n")
            errorMessage.append("Error Type: ${error.type}\n")
            errorMessage.append("Error Code: ${error.code}\n")
            errorMessage.append("Error Message: ${error.message}\n")
            errorMessage.append("Parameter: ${error.param}\n")
            
            if (!error.doc_url.isNullOrEmpty()) {
                errorMessage.append("Documentation: ${error.doc_url}\n")
            }
            
            if (!error.request_log_url.isNullOrEmpty()) {
                errorMessage.append("Request Log: ${error.request_log_url}\n")
            }
        }
        
        // Log detailed error for debugging
        Log.e("PayActivityTrack", "Detailed Error: $errorMessage")
        
        // Show detailed error in debug mode, simple error in release mode
        if (BuildConfig.DEBUG) {
            showDetailedErrorDialog(errorMessage.toString())
        } else {
            CommonUtilities.ShowToast(this@PayActivity, stripeUserModel.msg)
        }
    }

    private fun showDetailedErrorDialog(errorMessage: String) {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Payment Error (Debug Mode)")
        alertDialog.setMessage(errorMessage)
        alertDialog.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("Copy Error") { _, _ ->
            // Copy error to clipboard for debugging
            val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Stripe Error", errorMessage)
            clipboard.setPrimaryClip(clip)
            CommonUtilities.ShowToast(this, "Error copied to clipboard")
        }
        alertDialog.show()
    }


//        val cardInputWidget = findViewById<CardInputWidget>(R.id.cardInputWidget)
//        val params = cardInputWidget.paymentMethodCreateParams
//        if (params != null) {
//            val confirmParams = ConfirmPaymentIntentParams
//                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret)
//            stripe.confirmPayment(this, confirmParams)
//            controlPayBtnState(true)
//        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val weakActivity = WeakReference<Activity>(this)

        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
            override fun onSuccess(result: PaymentIntentResult) {
                controlPayBtnState(false)
                val paymentIntent = result.intent
                val status = paymentIntent.status
                if (status == StripeIntent.Status.Succeeded) {
                    val intent = Intent(this@PayActivity, ShareActivity::class.java)
                    startActivity(intent)
                } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                    displayAlert(weakActivity.get(), "Payment failed", paymentIntent.lastPaymentError?.message
                            ?: "")
                    Log.d("PayActivityTrack", "" + paymentIntent.lastPaymentError?.message)
                }
            }

            override fun onError(e: Exception) {
                controlPayBtnState(false)
                displayAlert(weakActivity.get(), "Payment failed", e.toString())
                e.printStackTrace()
                Log.d("PayActivityTrack", "onError: $e")
            }
        })
    }

    private fun controlPayBtnState(loading: Boolean){
        if(loading){
            payBtn.isEnabled = false
            payBtn.text = "Loading..."
        }else {
            payBtn.isEnabled = true
            payBtn.text = "Pay"
        }
    }

    private fun displayAlert(activity: Activity?, title: String, message: String) {
        if (activity == null) { return }
        runOnUiThread {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Retry", null)
            builder.setNegativeButton("Cancel Payment"){ p0, p1 ->
                finish()
            }
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}