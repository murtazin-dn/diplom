package com.example.diplom.presentation.services

import android.R
import android.app.Notification
import android.app.Notification.Builder.recoverBuilder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.example.diplom.domain.auth.usecase.IsMyUserIdUseCase
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.services.model.NotificationMessage
import com.example.diplom.util.BASE_URL
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject


class NotificationService : FirebaseMessagingService() {


    private val isMyId: IsMyUserIdUseCase by inject()

    companion object {
        const val CHANNEL_ID = "channelID"
        val CHANNEL_NAME = "channelName"
    }

    override fun onCreate() {
        super.onCreate()
        createNotifChannel()
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        println("meeeeeeeeeeeeeeeeeeesaaaaaaaaaageeeeeeeeee")

        if (message.data.isNotEmpty()) {

            val msg = decryptMessage(message.data)
            Log.e("msg", MainActivity.currentChatId.toString())
            if(MainActivity.currentChatId == msg.chat.id) return
            if(!isMyId.isMyUserId(msg.chat.firstUserId)) return
            MainActivity.getUnreadMessagesCount?.invoke()
            val person = createPerson(msg)
            val messageArrivalTime = System.currentTimeMillis()

            val activeNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                findActiveNotification(baseContext, msg.chat.id.toInt())
            } else null

            if (activeNotification != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                Log.e("msg", "m")

                val activeStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(activeNotification)
                val recoveredBuilder = NotificationCompat.Builder(baseContext, activeNotification)

                val newStyle = NotificationCompat.MessagingStyle(person)
                newStyle.conversationTitle = activeStyle!!.conversationTitle
                activeStyle.messages.forEach {
                    newStyle.addMessage(it.text, it.timestamp, it.person)
                }
                // Add your reply to the new style.
                newStyle.addMessage(msg.text, messageArrivalTime, person)

                // Set the new style to the recovered builder.
                recoveredBuilder.setStyle(newStyle)

                // Update the active notification.
                NotificationManagerCompat.from(baseContext).notify(msg.chat.id.toInt(), recoveredBuilder.build())
                Log.e("msg", "send")

            }else {

                val notification = NotificationCompat.Builder(baseContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_input_add)
                    .setStyle(
                        NotificationCompat.MessagingStyle(person)
                            .addMessage(msg.text, messageArrivalTime, person)
                    )
                    .build()
                NotificationManagerCompat.from(baseContext).notify(msg.chat.id.toInt(), notification)
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun findActiveNotification(context: Context, notificationId: Int): Notification? =
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .activeNotifications.find { it.id == notificationId }?.notification



    private fun createNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createPerson(msg: NotificationMessage) = Person.Builder().also{
            it.setKey(msg.user.id.toString())
            it.setName("${msg.user.name} ${msg.user.surname}")
            if(!msg.chat.secondUser.icon.isNullOrBlank()) {
                val imgAddress = "${BASE_URL}users/photo/${msg.chat.secondUser.icon}"
//
//                val bitmap = Glide
//                    .with(baseContext)
//                    .asBitmap()
//                    .load(imgAddress)
//                    .submit()
//                    .get()
//                it.setIcon(IconCompat.createWithAdaptiveBitmap(bitmap))
            }
        }.build()

    private fun decryptMessage(data: Map<String, String>): NotificationMessage {
        val str = data["msg"]!!
        return Json.decodeFromString<NotificationMessage>(str)
    }


    override fun onNewToken(token: String) {
        println("aaaaaaaaaaaaaaaaaaaaa")
        super.onNewToken(token)
        Log.e("token ", token)
    }

}