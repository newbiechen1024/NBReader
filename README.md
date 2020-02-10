# NBReader

参考 FBReader 的书籍阅读器。(当前只是半成品)

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
7. AndPermission:权限库
8. Tinypinyin：汉字转拼音库
9. LRecyclerView：RecyclerView 封装库
10. Retrofit：网络请求封装库