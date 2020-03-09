# NBBook 标签 (已重构，暂未更新文档)

该文档是针对 nbbook.so 文件解析文本，生成的中间 nbbook 数据结构文档。

标签分为：

1. resource：用到的资源信息。会添加到文本的头部。
2. body:内容展示信息。

资源标签有如下类型：

3. ImageAttr：标记为图片资源

内容有如下标签类型：

1. TextTag：文本标签。用于标记文本
2. ControlTag：控制标签。用于标记控制信息(如：文本类型信息)
3. ParagraphTag：段落标签。用于存储文本的段落信息。
4. ImageTag：图片标签

## Resource 标签

### ImageAttr

作用：图片资源信息。

数据结构：占用 (6 + 路径长度 + 编码长度)

| 资源类型 | 边缘对齐 | id | 路径文本长度 | 路径 | 0 是否编码存在 | 编码类型文字长度 | 编码名字 |

1. 资源类型：占用 1 字节。 image
2. 边缘对齐：占用 1 字节。
3. 资源 id：占 2 字节
4. 资源路径长：占 2 字节。
5. 路径文本：不固定
4. 编码相关暂时不支持

## Content 资源

### TextTag

作用：文本标签。用于标记文本

数据结构：占用 (6 + 文本字节数) 格式为 | 标签类型 | 边缘对齐 | 文本字节长度 | 文本内容
 
1. 标签类型：占用 1 字节。
2. 边缘对齐：占用 1 字节。 (好像不一定要边缘对齐，应该可以删除掉这个，之后再说)
3. 文本字节长度：占用 4 字节
4. 文本内容：占用文本长度字节。

### ControlTag

作用：控制标签。用于标记控制信息(如：文本类型信息)

数据结构：占用 4 字节，格式为 | 标签类型 | 边缘对齐 | 样式标签 | 是开放标签还是闭合标签 |
 
1. 标签类型：占用 1 字节。
2. 边缘对齐：占用 1 字节。
3. 样式标签：占用 1 字节 ==> 详见 TextParagraph::Type
4. 标签类型：占用 1 字节 ==> 0 或者是 1

### ParagraphTag (待定，后续会修改)

数据结构：占用 4 字节，格式为 | 标签类型 | 边对齐 | 段落类型 | 边缘对齐 |

1. 标签类型：占用 1 字节
2. 边缘对齐：占用 1 字节。
3. 段落类型：占用 1 字节 ==> 详见 TextParagraph::Type
4. 边缘对齐：占用 1 字节 ==> 0

### StyleTag 

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

### StyleCloseTag

作用：用于 StyleTag 的闭合标签，标记样式结束位置。

数据结构：

1. 标签类型：占用 1 字节
2. 边缘对齐：占用 1 字节 ==> 0

### FixHSpaceTag

作用：竖直区域留白标签。相当于 margin 的作用。

数据结构：

1. tag 类型：占用 1 字节
2. 对齐填充：占用 1 字节 
3. 竖直距离：占用 1 字节 
4. 对齐填充：占用 1 字节 

TODO:之后需要把所有边缘对齐部分给删掉。

### ImageTag (未实现，占位)

作用：图片标签，用于显示图片。

数据结构：占 4 字节。 | 标签类型 | 边缘对齐 | id

1. tag 类型：占用 1 字节。
2. 边缘对齐：占用 1 字节。
3. 资源 id：占用 2 字节。

### HyperlinkControlTag (未实现，占位)

作用：超链接标签，用于跳转

数据结构：

1. tag 类型：占用 1 字节
2. 对齐填充：占用 1 字节 