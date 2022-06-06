package com.android.locate

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hypertrack.sdk.HyperTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn1: Button = findViewById(R.id.button)
        val btn2: Button =findViewById(R.id.button2)
        val text: TextView = findViewById(R.id.textView)

        val sdkInstance =
            HyperTrack.getInstance("i2vS3WWXmKSmKE6kfpK7Vmh76q_wO7ER6R9eNYkS3_neVxPzk5WUfW9ERqZpsyolO3YDlXPDncCAzqntt64r0A")
        sdkInstance.requestPermissionsIfNecessary()
        val deviceId = sdkInstance.deviceID

        //crediantials
        val user = "xFUs5siof9KXnAhKmhV7X7lt-NM"
        val password = "yvwxy9zDhEFSAQz6YknbaWPfP6KLnqr3MDw0hMtK38_IuSIC9Jskgg"

        // Set device name as in the example above
        sdkInstance.setDeviceName("Kamal sharma")
        // create new metadata map
        val myMetadata: Map<String, Any> =
            mutableMapOf("vehicle_type" to "scooter", "group_id" to 1)
        sdkInstance.setDeviceMetadata(myMetadata);

        class BasicAuthInterceptor(user: String, password: String): Interceptor {
            private var credentials:String= Credentials.basic(user,password)
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request = chain.request()
                val authenticatedRequest: Request = request.newBuilder()
                    .header("Authorization", credentials).build()
                return chain.proceed(authenticatedRequest)
            }
        }

       // val body = " ".toRequestBody(null)
        val jsonObject = JSONObject()
        jsonObject.put("vehicle_type", "scooter")
        jsonObject.put("group_id", 1)

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        val requestBody=jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor("xFUs5siof9KXnAhKmhV7X7lt-NM", "yvwxy9zDhEFSAQz6YknbaWPfP6KLnqr3MDw0hMtK38_IuSIC9Jskgg"))
            .build()


        fun onStart(){
            val request: Request = Request.Builder()
                .url("https://v3.api.hypertrack.com/devices/$deviceId/start")
                .post(requestBody)
                .build()

            val response: Response = client.newCall(request).execute()

            System.out.println(response.body?.string() ?:"no response" )
        }

        fun onStop(){
            val request: Request = Request.Builder()
                .url("https://v3.api.hypertrack.com/devices/$deviceId/stop")
                .post(requestBody)
                .build()
            val response: Response = client.newCall(request).execute()
            println(response.body?.string())
        }


        btn1.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch{
                onStart()
            }
            text.setText("location tracking started ")
        }

        btn2.setOnClickListener{
            CoroutineScope(Dispatchers.Default).launch {
                onStop()
            }
            text.setText("Location Tracking stopped")
        }


    }
}
