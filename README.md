# light-rpc

light-rpc 是一个基于 Netty 实现的轻量级 RPC 框架。个人进行 RPC 和 Netty 时进行的实践项目。

主要借鉴了国内两款优秀的 PRC 框架：[Dubbo](https://github.com/apache/dubbo) 和 [Motan](https://github.com/weibocom/motan)。

### 架构
![Architecture](https://user-images.githubusercontent.com/22571230/191029159-b5a91d0a-80ae-4737-a9a5-04a513901b83.png)

### 特性

- 基于 Netty(NIO) 实现网络传输；
- 使用 Zookeeper 实现注册中心；
- 四种序列化方式：Hessian2、JSON、Kryo、Protobuf；
- 三种负载均衡算法：随机算法、（加权）轮询算法、一致性哈希算法；
- 三种数据压缩算法：Gzip、Bzip2、Snappy；

### 传输协议设计

考虑到 TCP 沾包问题，使用定长头+字节数组作为自定义编码协议，使用 Netty 提供的 `LengthFieldBasedFrameDecoder` 类作为解码器。

```txt
      0     1     2     3      4        5        6        7         8   9   10   11    12    13    14   15
   +-----+-----+-----+-----+-------+--------+-------+-------------+---+---+----+----+-----+-----+-----+----+
   |   magic   number      |version|compress| codec | messageType |   messageId     |     dataLength       |
   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
   |                                                                                                       |
   |                                         data                                                          |
   |                                                                                                       |
   |                                        ... ...                                                        |
   +-------------------------------------------------------------------------------------------------------+
 4Byte magic number（魔法数）  1Byte version（版本）     1Byte compress（压缩类型）  1Byte codec（序列化类型）
 1Byte messageType（消息类型） 4Byte messageId（消息Id） 4Byte dataLength（消息长度）data（object类型数据）
```


