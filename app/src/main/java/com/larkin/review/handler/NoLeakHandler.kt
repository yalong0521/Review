package com.larkin.review.handler

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference

class NoLeakHandler(looper: Looper, handlerFunc: HandlerFunc) : Handler(looper) {
    private val handlerFunctionRef = WeakReference(handlerFunc)

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        handlerFunctionRef.get()?.handleMessage(msg)
    }

    interface HandlerFunc {
        fun handleMessage(msg: Message)
    }
}

