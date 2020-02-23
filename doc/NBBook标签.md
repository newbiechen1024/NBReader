# NBBook 标签

该文档是针对 nbbook.so 文件解析文本，生成的中间 nbbook 数据结构文档。

nbbook 当前存在如下标签类型：

1. TextTag：文本标签。用于标记文本
2. ControlTag：控制标签。用于标记控制信息(如：文本类型信息)
3. ParagraphTag：段落标签。用于存储文本的段落信息。

## TextTag

作用：文本标签。用于标记文本

数据结构：占用 (6 + 文本字节数) 格式为 | 标签类型 | 边缘对齐 | 文本字节长度 | 文本内容
 
1. 标签类型：占用 1 字节。
2. 边缘对齐：占用 1 字节。 (好像不一定要边缘对齐，应该可以删除掉这个，之后再说)
3. 文本字节长度：占用 4 字节
4. 文本内容：占用文本长度字节。

## ControlTag

作用：控制标签。用于标记控制信息(如：文本类型信息)

数据结构：占用 4 字节，格式为 | 标签类型 | 边缘对齐 | 样式标签 | 是开放标签还是闭合标签 |
 
1. 标签类型：占用 1 字节。
2. 边缘对齐：占用 1 字节。
3. 样式标签：占用 1 字节 ==> 详见 TextParagraph::Type
4. 标签类型：占用 1 字节 ==> 0 或者是 1

## ParagraphTag (待定，后续会修改)

数据结构：占用 4 字节，格式为 | 标签类型 | 边对齐 | 段落类型 | 边缘对齐 |

1. 标签类型：占用 1 字节
2. 边缘对齐：占用 1 字节。
3. 段落类型：占用 1 字节 ==> 详见 TextParagraph::Type
4. 边缘对齐：占用 1 字节 ==> 0

## StyleTag 

作用：样式标签。设置文本的渲染样式

数据结构：

1. style 类型：占 1 字节。 style 类型有 StyleCss 和 StyleOther 两种
2. 边缘对齐：占用 1 字节。
3. depth 信息：占 1 字节。 style 深度。
4  边缘对齐：占用 1 字节。
5. featureMask 信息：占 4 字节。style 包含的样式标记。样式类型详见 TextFeature
6. 对于 TextFeature 标记的前 9 种类型，是否被标记，如果被标记，则填充样式数值信息：占 4 字节。
7. 对于 TextFeature 标记的后 3 种类型，是否被标记，如果被标记，则填充样式数值信息：占 2 字节。

Style 支持的样式类型：

1. LENGTH_PADDING_LEFT = 0,
2. LENGTH_PADDING_RIGHT = 1,
3. LENGTH_MARGIN_LEFT = 2,
4. LENGTH_MARGIN_RIGHT = 3,
4. LENGTH_FIRST_LINE_INDENT = 4,
5. LENGTH_SPACE_BEFORE = 5,
6. LENGTH_SPACE_AFTER = 6,
7. LENGTH_FONT_SIZE = 7,
8. LENGTH_VERTICAL_ALIGN = 8,
9. NUMBER_OF_LENGTHS = 9, // 中断标记，上面是占 4 字节，下面是占 2 字节.
10. ALIGNMENT_TYPE = NUMBER_OF_LENGTHS + 0,
11. FONT_FAMILY = NUMBER_OF_LENGTHS + 1,
12. FONT_STYLE_MODIFIER = NUMBER_OF_LENGTHS + 2,
13. NON_LENGTH_VERTICAL_ALIGN = NUMBER_OF_LENGTHS + 3,
14. DISPLAY = NUMBER_OF_LENGTHS + 4 // 11; max = 15

## StyleCloseTag

作用：用于 StyleTag 的闭合标签，标记样式结束位置。

数据结构：

1. 标签类型：占用 1 字节
2. 边缘对齐：占用 1 字节 ==> 0

## FixHSpace

作用：竖直区域留白标签。相当于 margin 的作用。

数据结构：

1. tag 类型：占用 1 字节
2. 对齐填充：占用 1 字节 
3. 竖直距离：占用 1 字节 
4. 对齐填充：占用 1 字节 

TODO:之后需要把所有边缘对齐部分给删掉。