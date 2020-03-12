# NBReader

自定义 Android 书籍渲染引擎的文本阅读器。(整体架构参考  [FBReaderJ](https://github.com/geometer/FBReaderJ) )

**注：当前只是半成品，仅支持 arm-v7a 架构**

## 特性

* 使用 C++ 实现文本格式解析，当前支持处理 TXT、EPUB 格式
* 实现 C++ 层书籍文件进行章节分割，分章解析，加快解析速度。
* 在 C++ 层将文本数据统一解析成一套 nbbook 标签，上层统一处理 nbbook 标签。
* Java 层解析标签实现富文本显示
* 实现 PageView 文本显示类，支持添加自定义 HeaderView 和 FooterView，支持多种翻页效果(仿真、滚动、覆盖、滑动)
* 实现 TextConfig 支持自定义设置标签样式
* 实现 PageController 类支持加载书籍(暂不支持加载网络书籍)、自定义分章正则、页面跳转、获取翻页监听、获取页面点击事件等逻辑

## 演示

翻页效果：

![](https://github.com/newbiechen1024/NBReader/blob/master/doc/image/book_show.gif?raw=true)

## 使用

### 加入 XML

```
<com.newbiechen.nbreader.ui.component.widget.page.PageView
    android:id="@+id/pv_book"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

### 添加 HeaderView 和 FooterView

```
// 创建 HeaderView
val pageHeaderView =
    LayoutInflater.from(this).inflate(R.layout.layout_page_header, pageView, false)
mTvPageTitle = pageHeaderView.findViewById(R.id.tv_title)

// 创建 FooterView
val pageFooterView =
    LayoutInflater.from(this).inflate(R.layout.layout_page_footer, pageView, false)

// 添加 Header 和 Footer
mPageView.setHeaderView(pageHeaderView)
mPageView.setFooterView(pageFooterView)
```

### 设置翻页效果

```
// 仿真翻页
val pageAnimType =  PageAnimType.SIMULATION
// 设置翻页动画
pvBook.setPageAnim(pageAnimType)
```

### 打开关闭书籍

```
// 获取页面控制器
val pageController:PageController = mPageView.getPageController()

// 设置书籍参数
val bookPath:String = "xxxxxx"
val bookType:BookType = BookType.TXT

// 打开书籍
pageController.open(bookPath, bookType)

// 关闭书籍 (由于会在 JNI 中创建一个持久化对象，需要执行释放操作)
pageController.close()
```

###  监听页面回调

```
// 设置页面监听
pageController.setPageListener(object : OnPageListener {
    // PagePosition：当前页面位置
    // PageProgress：当前页面在书籍的总进度
    override fun onPreparePage(pagePosition: PagePosition, pageProgress: PageProgress) {
        // 页面准备回调
    }

    override fun onPageChange(pagePosition: PagePosition, pageProgress: PageProgress) {
    // 页面翻页改变回调
    }
})
```

###  自定义标签样式

```
// 创建文本配置项
val textConfig = TextConfig.Builder(this.applicationContext)
    .configure(TextConfigure)   // 页面配置信息
    .defaultStyle(DefaultTextStyle) // 默认标签配置信息
    .controlStyleInterceptor(ControlStyleInterceptor) // 控制标签样式链接器，可自定义返回控制标签样式
    .cssStyleInterceptor(CSSStyleInterceptor)  // CSS 标签拦截器，支持禁止 CSS 属性使用。
    .build()

// 配置加入到页面控制器中
pageController.setTextConfig(textConfig)
```

## TODO

- [ ] 支持更多 html 和 css 标签
- [ ] 支持自定义 View 加入到文本流中
- [ ] 支持 openBook() 自定义输入源

## nbbook 文档

文档：

* [自定义的 nbbook 标签](https://github.com/newbiechen1024/NBReader/blob/master/doc/custom_nbbook_tag.md)
* [支持的 HTML 标签](https://github.com/newbiechen1024/NBReader/blob/master/doc/support_html_tag.md)
* [支持的 CSS 属性](https://github.com/newbiechen1024/NBReader/blob/master/doc/support_css_attribute.md)

## NBReader 框架图

![框架图](https://raw.githubusercontent.com/newbiechen1024/NBReader/master/doc/image/nbreader_framework.png)

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