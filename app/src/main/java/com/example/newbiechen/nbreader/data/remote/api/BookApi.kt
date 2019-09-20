package com.example.newbiechen.nbreader.data.remote.api

import com.example.newbiechen.nbreader.data.entity.NetBookDetailWrapper
import com.example.newbiechen.nbreader.data.entity.NetBookListWrapper
import com.example.newbiechen.nbreader.data.entity.CatalogLabelWrapper
import com.example.newbiechen.nbreader.data.entity.CatalogWrapper
import com.example.newbiechen.nbreader.uilts.Constants
import io.reactivex.Flowable
import retrofit2.http.*

interface BookApi {

    /**
     * 获取分类
     * @return
     */
    @GET("/category/statics")
    fun getCatalog(): Flowable<CatalogWrapper>

    /**
     * 获取分类下的标签
     */
    @GET("/category/cats")
    fun getCatalogLabel(): Flowable<CatalogLabelWrapper>

    /**
     * 根据类别获取书籍列表
     *
     * alias：分类名：从 /category/statics 下获取
     * sort：排序方式:人气、评分等
     * start：起始数据
     * limit:请求多少条数据
     * cat：子标签
     * isserial：状态  true 连载 / false 完结
     * updated：更新时间: 3天、7天、15天
     *
     */
    @GET("/category/fuzzy-search")
    fun getBookList(
        @Query("alias") alias: String, @Query("sort") sort: Int, @Query("cat") cat: String?,
        @Query("isserial") isserial: Boolean?, @Query("updated") updated: Int?,
        @Query("start") start: Int, @Query("limit") limit: Int
    ): Flowable<NetBookListWrapper>

    @Headers("baseUrl:${Constants.LABEL_MORE_BASE_URL}")
    @GET("book/{bookId}?useNewCat=true")
    fun getBookDetail(@Path("bookId") bookId: String): Flowable<NetBookDetailWrapper>
}