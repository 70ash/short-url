# short-url
短链接是指将一个原始的长 URL（统一资源定位符）通过特定的算法或服务转化为一个更短、易于记忆的 URL。短链接通常只包含几个字符，而原始的长 URL 可能会非常长。

短链接的原理如下：

生成唯一标识符：当用户输入或提交一个长 URL 时，短链接服务会生成一个唯一的标识符或短码。
将标识符与长 URL 关联：短链接服务将这个唯一标识符与用户提供的长 URL 关联起来，并保存在数据库或其他持久化存储中。
创建短链接：将生成的唯一标识符加上短链接服务的域名（例如：http://70ash.top ）作为前缀，构成一个短链接。
重定向：当用户访问该短链接时，短链接服务接收到请求后会根据唯一标识符查找关联的长 URL，然后将用户重定向到这个长 URL。
跟踪统计：一些短链接服务还会提供访问统计和分析功能，记录访问量、来源、地理位置等信息。
短链接经常出现在日常生活中，尤其在活动节日的营销短信中，帮助企业在营销活动中识别用户行为、点击率等关键信息监控。

短链接的主要作用包括：

提升用户体验：用户更容易记忆和分享短链接，增强了用户的体验。
节省空间：短链接比长 URL 更短，可以节省字符空间，特别是在一些限制字符数的场合，如微博、短信等。
美化：短链接通常更美观、简洁，不会包含一大串字符。
统计和分析：可以追踪短链接的访问情况，了解用户的行为和喜好。
## 技术架构
在系统设计中，我们采用了最新的 JDK17 和 SpringBoot3 & SpringCloud 微服务架构，以构建一个在高并发、大数据量环境下仍然能提供高效可靠的短链接生成服务。
