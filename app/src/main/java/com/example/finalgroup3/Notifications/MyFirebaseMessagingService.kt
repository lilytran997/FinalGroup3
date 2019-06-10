package com.example.finalgroup3.Notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.example.finalgroup3.MessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        remoteMessage?.data?.isNotEmpty()?.let {

            val sent = remoteMessage.data["sent"]

            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (!(firebaseUser == null || sent != firebaseUser.uid)) {
                sendNotifications(remoteMessage)
            }
        }

    }

    private fun sendNotifications(remoteMessage: RemoteMessage) {
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val notification = remoteMessage.notification
        val j = Integer.parseInt(user!!.replace("[\\D]".toRegex(), ""))
        val intent = Intent(this, MessageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this)
            .setSmallIcon(Integer.parseInt(icon))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(sound)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var i = 0
        if (j > 0) {
            i = j
        }
        notificationManager.notify(i, builder.build())
    }
    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.e("kiem tra token", "Refreshed token: " + token!!)
        sendRegistrationToServer(token)
    }
    private fun sendRegistrationToServer(token: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance().getReference("Tokens")
        val newToken = Token(token!!)
        assert(user != null)
        database.child(user!!.uid).setValue(newToken)

    }

}