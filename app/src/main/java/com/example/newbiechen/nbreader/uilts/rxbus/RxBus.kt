package com.example.newbiechen.nbreader.uilts.rxbus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 *  author : newbiechen
 *  date : 2020-02-06 21:07
 *  description :全局事件传递
 */

/**
 * RxBus 事件标记
 */
interface RxEvent

class RxBus {

    companion object {
        @Volatile
        private var instance: RxBus? = null

        fun getInstance() = instance ?: synchronized(this) {
            RxBus().also { instance = it }
        }
    }

    private val mEventBus = PublishSubject.create<RxEvent>()

    /**
     * 发送事件(post event)
     * @param event : event object(事件的内容)
     */
    fun post(event: RxEvent) {
        mEventBus.onNext(event)
    }

    /**
     *
     * @param code
     * @param event
     */
    fun post(code: Int, event: RxEvent?) {
        val msg = Message(code, event!!)
        mEventBus.onNext(msg)
    }

    /**
     * 返回Event的管理者,进行对事件的接受
     * @return
     */
    fun toObservable(): Observable<RxEvent> {
        return mEventBus
    }

    /**
     * 监听类型
     * @param cls :保证接受到制定的类型
     * */
    fun <T : RxEvent> toObservable(cls: Class<T>?): Observable<T> { //ofType起到过滤的作用,确定接受的类型
        return mEventBus.ofType(cls)
    }

    fun <T : RxEvent> toObservable(code: Int, cls: Class<T>): Observable<T> {
        return mEventBus.ofType(Message::class.java)
            .filter { msg: Message ->
                msg.code == code && cls.isInstance(
                    msg.event
                )
            }
            .map { msg: Message -> msg.event as T }
    }

    private inner class Message(val code: Int, val event: RxEvent) : RxEvent
}