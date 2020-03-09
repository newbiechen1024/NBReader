# HTML:支持的标签(3月9日)

HTML 标签划分为两类：

1. 功能标签：有布局标签(如：body、div)、资源标签(如：img、video)
2. 控制标签：控制内容样式的标签，如 h1 ~ h6、strong、em、code 等。

下面描述的标签，并不是每个都支持，还需要处理

## 功能标签

主要有如下标签：

1. body
2. aside
3. style
4. ol：
5. ul：
6. li：
7. a：
8. img：
9. object：
10. image:
11. svg：
12. div：
13. dt：
14. link：
15. pre：
16. td：
17. th：
19. source：

## 控制标签

主要有如下标签：

1. p:
2. h1：
3. h2：
4. h3：
5. h4：
6. h5：
7. h6：
8. strong：
9. b：
10. em：
11. i：
12. code：
13. tt：
14. kbd：
15. var：
16. samp：
17. cite
18. sup：
19. sub：
20. dd：
21. dfn：
22. strike：

这些控制标签对应 NBReader 的 ControlTag ，每个标签样式对应 TextKind。

支持使用 control.css 配置 TextKind 的样式。