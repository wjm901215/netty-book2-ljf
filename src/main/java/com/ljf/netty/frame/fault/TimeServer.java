/*
 * Copyright 2013-2018 Lilinfeng.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ljf.netty.frame.fault;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author lilinfeng
 * @version 1.0
 * @date 2014年2月14日
 */
public class TimeServer {

    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组,用于网络事件的处理，实际上它们就是Reactor线程组”
        //“创建两个的原因是一个用于服务端接受客户端的连接，另一个用于进行SocketChannel的网络读写”
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        	//“Netty用于启动NIO服务端的辅助启动类，目的是降低服务端的开发复杂度”
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
					//“对应于JDK NIO类库中的ServerSocketChannel类”
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
					//“作用类似于Reactor模式中的handler类，主要用于处理网络I/O事件，例如记录日志、对消息进行编解码等”
                    .childHandler(new ChildChannelHandler());
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            // 阻塞，等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            arg0.pipeline().addLast(new TimeServerHandler());
        }

    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new TimeServer().bind(port);
    }
}
