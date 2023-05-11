package com.example.diplom.data.network.messages.repository

import android.util.Log
import com.example.diplom.util.TokenService
import com.example.diplom.util.WEB_SOCKET_URL
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

class WebSocketClient(
    private val tokenService: TokenService
) {
    private lateinit var webSocket: okhttp3.WebSocket
    private var socketListener: SocketListener? = null
    private var shouldReconnect = true
    private var client: OkHttpClient? = null

    private var chatId: Long? = null


    fun setMessageListener(listener: SocketListener) {
        this.socketListener = listener
    }
    fun setChatId(chatId: Long) {
        this.chatId = chatId
    }




    private fun initWebSocket() {
        val token = tokenService.getToken()
        val socketUrl = WEB_SOCKET_URL + chatId!!
        Log.e("socketCheck", "initWebSocket() socketurl = $socketUrl")
        client = OkHttpClient()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val request = Request.Builder()
            .addHeader("Authorization", "Bearer $token")
            .url(url = socketUrl)
            .build()
        println(request)
        webSocket = client!!
            .newWebSocket(request, webSocketListener)
        //this must me done else memory leak will be caused
        client!!.dispatcher.executorService.shutdown()
    }



    fun connect() {
        Log.e("socketCheck", "connect()")
        shouldReconnect = true
        initWebSocket()
    }

    fun reconnect() {
        Log.e("socketCheck", "reconnect()")
        initWebSocket()
    }

    //send
    fun sendMessage(message: String) {
        Log.e("socketCheck", "sendMessage($message)")
        if (::webSocket.isInitialized) webSocket.send(message)
    }


    //We can close socket by two way:

    //1. websocket.webSocket.close(1000, "Dont need connection")
    //This attempts to initiate a graceful shutdown of this web socket.
    //Any already-enqueued messages will be transmitted before the close message is sent but
    //subsequent calls to send will return false and their messages will not be enqueued.

    //2. websocket.cancel()
    //This immediately and violently release resources held by this web socket,
    //discarding any enqueued messages.

    //Both does nothing if the web socket has already been closed or canceled.
    fun disconnect() {
        if (::webSocket.isInitialized) webSocket.close(1000, "Do not need connection anymore.")
        shouldReconnect = false
    }

    interface SocketListener {
        fun onMessage(message: String)
    }


    private val webSocketListener = object : WebSocketListener() {
        //called when connection succeeded
        //we are sending a message just after the socket is opened
        override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
            Log.e("socketCheck", "onOpen()")
        }

        //called when text message received
        override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
            socketListener?.onMessage(text)
        }

        //called when binary message received
        override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
            Log.e("socketCheck", "onClosing()")
        }

        override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
            //called when no more messages and the connection should be released
            Log.e("socketCheck", "onClosed()")
            if (shouldReconnect) reconnect()
        }

        override fun onFailure(
            webSocket: okhttp3.WebSocket, t: Throwable, response: Response?
        ) {
            Log.e("socketCheck", "onFailure() $t")
            if (shouldReconnect) reconnect()
        }
    }
}