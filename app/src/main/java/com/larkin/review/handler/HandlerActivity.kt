package com.larkin.review.handler

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.larkin.review.R

/**
 * P1:为什么一个线程只能有一个Looper?
 *      首先Looper的内部实现限制了一个线程只能有一个Looper,具体见Looper.prepare()；
 *      其次考虑到为什么要做该限制，究其原因在于Looper.loop()方法，该方法会通过死循环对当前线程消息队列进行轮训，
 *      最终会导致该线程阻塞，故多个Looper是没有意义的，所以要限制一哥线程只能有一个Looper。
 * P2:什么是同步屏障，Handler发送异步消息的使用场景是什么？
 *      msg.target == null 即为同步屏障，表示要优先执行异步消息，阻塞同步消息
 *      使用场景：Android4.1之后增加了Choreographer机制，用于同 Vsync 机制配合，统一动画、输入和绘制时机。
 *      在ViewRootImpl的requestLayout()->scheduleTraversals()中可以找到答案
 */
class HandlerActivity : AppCompatActivity(R.layout.activity_main), NoLeakHandler.HandlerFunc {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val mHandler = NoLeakHandler(Looper.getMainLooper(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 发送同步屏障
        val token = postSyncBarrier()
        Log.i(TAG, "同步屏障加入成功，token=${token}")
        // 延迟3S发送一条异步消息
        val asyncMsg = Message.obtain()
        asyncMsg.isAsynchronous = true
        asyncMsg.what = 521
        asyncMsg.arg1 = token
        mHandler.sendMessageDelayed(asyncMsg, 3000)
        // 发送一条立即执行的同步消息
        val syncMsg = Message.obtain()
        syncMsg.what = 110
        mHandler.sendMessage(syncMsg)
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun handleMessage(msg: Message) {
        if (msg.what == 521) {
            Log.i(TAG, "收到加入同步屏障后发来的异步消息了，现在开始移除同步屏障")
            removeSyncBarrier(msg.arg1)
        } else if (msg.what == 110) {
            Log.i(TAG, "同步消息在异步消息执行后执行了")
        }
    }

    /**
     * 主线程发送同步屏障
     * @return token
     */
    @SuppressLint("DiscouragedPrivateApi")
    private fun postSyncBarrier(): Int {
        val queue = Looper.getMainLooper().queue
        val post = queue.javaClass.getDeclaredMethod("postSyncBarrier")
        post.isAccessible = true
        return post.invoke(queue) as Int
    }

    /**
     * 主线程移除同步屏障
     */
    @SuppressLint("DiscouragedPrivateApi")
    private fun removeSyncBarrier(token: Int) {
        val queue = Looper.getMainLooper().queue
        val remove = queue.javaClass.getDeclaredMethod("removeSyncBarrier", Int::class.java)
        remove.isAccessible = true
        remove.invoke(queue, token)
    }

    fun onClick(view: View) {
        Log.i(TAG, "点击了")
    }
}