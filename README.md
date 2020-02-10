# NBReader

参考 FBReader 的书籍阅读器。(当前只是半成品)

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
7. AndPermission:权限库
8. Tinypinyin：汉字转拼音库
9. LRecyclerView：RecyclerView 封装库
10. Retrofit：网络请求封装库