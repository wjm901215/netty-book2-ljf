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
package com.ljf.netty.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author lilinfeng
 * @version 1.0
 * @date 2014年2月16日
 */
public class AcceptCompletionHandler implements
        CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    @Override
    public void completed(AsynchronousSocketChannel result,
                          AsyncTimeServerHandler attachment) {
        /*
        * 从attachment获取成员变量AsynchronousServerSocketChannel，然后继续调用它的accept方法
        *“既然已经接收客户端成功了，为什么还要再次调用accept方法呢？
        * 原因是这样的：当我们调用AsynchronousServerSocketChannel的accept方法后，如果有新的客户端连接接入，
        * 系统将回调我们传入的CompletionHandler实例的completed方法，表示新的客户端已经接入成功，
        * 因为一个AsynchronousServerSocket Channel可以接收成千上万个客户端，
        * 所以我们需要继续调用它的accept方法，接收其他的客户端连接，最终形成一个循环。
        * 每当接收一个客户读连接成功之后，再异步接收新的客户端连接
         */
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }

}
