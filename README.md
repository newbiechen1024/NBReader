# NBReader

参考 FBReader 的书籍阅读器。(当前只是半成品，仅支持 arm-v7a 架构)

当前支持书籍格式：epub、txt

## 演示

获取书籍文件：

![](https://github.com/newbiechen1024/NBReader/blob/master/doc/image/%E8%8E%B7%E5%8F%96%E6%9C%AC%E5%9C%B0%E4%B9%A6%E7%B1%8D.gif?raw=true)

展示 epub 翻页：

![](https://github.com/newbiechen1024/NBReader/blob/master/doc/image/%E4%B9%A6%E7%B1%8D%E7%BF%BB%E9%98%85_epub.gif?raw=true)

## 使用

书籍显示类 PageView ：

1. 添加到 xml 中
    
   ```
   <androidx.constraintlayout.widget.ConstraintLayout
       android:layout_width="match_parent"
       android:layout_height="match_parent">
       
       <!--绘制内容-->
       <com.newbiechen.nbreader.ui.component.widget.page.PageView
           android:id="@+id/pv_book"
           android:layout_width="0dp"
           android:layout_height="0dp"
           app:layout_constraintBottom_toBottomOf="parent"
           app:layout_constraintLeft_toLeftOf="parent"
           app:layout_constraintRight_toRightOf="parent"
           app:layout_constraintTop_toTopOf="parent" />
           
       <!--省略-->
   </androidx.constraintlayout.widget.ConstraintLayout>
   ```

2. 在 Activity 中控制

   ```
    pageView.apply {
            // 设置顶部 View
            setHeaderView(headerBinding.root)
            // 设置底部 View
            setFooterView(footerBinding.root)
    
            // 设置点击事件监听
            setActionListener { action:PageAction -> 
                // 处理
            }
    
            // 页面准备监听
            setOnPreparePageListener { pagePosition, pageProgress ->
                // pagePosition 页面位置信息
                // pageProgress 页面进度信息
            }
            
            //页面控制器
            PageController controler = getPageController() 
     }
   ```

页面控制加载类 PagController 提供的方法:

```
class PageController {
    /**
     * 设置配置项，必须在 open() 之前设置
     */
    fun setConfigure(
        cachePath: String,
        initChapterTitle: String,
        chapterPattern: String
    )

    /**
     * 设置页面样式
     */
    fun setPageStyle() 
    
    /**
     * 设置页面回调
     */
    fun setPageListener() 

    /**
     * 打开本地书籍
     * @param bookPath：只支持本地书籍
     */
    fun open(bookPath: String, bookType: BookType)

    /**
     * 打开自定义书籍(网络等)
     */
    fun open(bookGroup: BookGroup, bookType: BookType)

    /**
     * 关闭书籍
     */
    fun close()

    /**
     * 跳转页面
     */
    fun skipPage(type: PageType) 

    /**
     * 跳转章节
     */
    fun skipChapter(type: PageType) 

    /**
     * 跳转章节
     */
    fun skipChapter(index: Int) 


    /**
     * 是否书籍已经打开
     */
    fun isOpen(): Boolean 

    /**
     * 获取书籍章节列表
     */
    fun getChapters(): Array<TextChapter> 

    /**
     * 获取当前章节，获取当前章节索引
     */
    fun getCurChapterIndex(): Int 

    /**
     * 获取当前页面索引
     */
    fun getCurPageIndex(): Int 

    /**
     * 获取当前页面数量
     */
    fun getCurPageCount(): Int 

    /**
     * 获取当前的定位
     */
    fun getCurPosition() 

    /**
     * 是否是支持的书籍类型
     */
    fun isSupportBookType(type: BookType): Boolean

    /**
     * 获取支持的书籍类型
     */
    fun getSupportBookType(): List<BookType> 

    /**
     * 是否页面存在
     */
    fun hasPage(type: PageType): Boolean 
    
    /**
     * 是否章节存在
     */
    fun hasChapter(type: PageType): Boolean
}
```

## NBReader 大致框架

![框架图](https://raw.githubusercontent.com/newbiechen1024/NBReader/master/doc/image/NBReader%E6%A1%86%E6%9E%B6%E5%A4%A7%E8%87%B4%E7%BB%93%E6%9E%84.png)

## nbbook 文档

文档：

* [中间文件 TAG 标签](https://github.com/newbiechen1024/NBReader/blob/master/doc/NBBook%E6%A0%87%E7%AD%BE.md)

## 采用框架

native 框架：

1. libexpat：xml 解析库
2. libiconv：编码转换库
3. liblinebreak：文字分割库
4. libonig：正则匹配库(测试过原生、re 库，这个库对于处理 utf-8 匹配的效率相对较快)
5. libuchardet：文字编码检测库

Java 层框架：

1. DataBinding：采用 MVVM 模式开发。
2. Dagger2：依赖注入库
3. RxJava：代码风格库
4. Room：数据库
5. OkHttp：网络库
6. Glide：图片加载库
7. [AndPermission](https://github.com/yanzhenjie/AndPermission):权限库
8. [Tinypinyin](https://github.com/promeG/TinyPinyin)：汉字转拼音库
9. [LRecyclerView](https://github.com/jdsjlzx/LRecyclerView)：RecyclerView 封装库
10. Retrofit：网络请求封装库
11. [zip4j](https://github.com/srikanth-lingala/zip4j):zip 文件处理库