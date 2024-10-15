package com.example.alumnijobapp.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle the message, such as showing a notification
        Log.d("FCM", "Message received: ${remoteMessage.notification?.body}")
    }

    override fun onNewToken(token: String) {
        // Handle token refresh
        Log.d("FCM", "New token: $token")
        // Send token to your server or save it in shared preferences
    }
}