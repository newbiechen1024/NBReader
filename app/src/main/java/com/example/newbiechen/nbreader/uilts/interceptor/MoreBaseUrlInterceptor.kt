package com.example.newbiechen.nbreader.uilts.interceptor

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 *  author : newbiechen
 *  date : 2019-08-13 19:41
 *  description:解决具有多个 baseurl 的问题
 */

class MoreBaseUrlInterceptor(private val urlMap: Map<String, String>) : Interceptor {
    companion object {
        private const val HEAD_BASE_URL = "baseUrl"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // 获取原始的 request
        val originRequest = chain.request()
        // 获取原始的 HttpUrl
        val originHttpUrl = originRequest.url
        // 获取 origin 的 builder
        val requestBuilder = originRequest.newBuilder()
        // 查找名为 baseUrl 的 header
        val urlHeaders = originRequest.headers(HEAD_BASE_URL)
        if (urlHeaders.isNotEmpty()) {
            // 从 builder 中移除 baseUrl header 信息
            requestBuilder.removeHeader(HEAD_BASE_URL)
            // 获取匹配到的第一个 baseUrl 路径，并从 map 中查找
            val newUrl = urlMap[urlHeaders[0]]
            return if (newUrl != null) {
                val newUri = newUrl.toHttpUrl()
                // 生成新的 httpUrl
                val newHttpUrl = originHttpUrl.newBuilder()
                    .scheme(newUri.scheme)
                    .host(newUri.host)
                    .port(newUri.port)
                    .build()
                // 生成新的 Request 并发送
                chain.proceed(requestBuilder.url(newHttpUrl).build())
            } else {
                chain.proceed(originRequest)
            }
        } else {
            return chain.proceed(originRequest)
        }
    }
}