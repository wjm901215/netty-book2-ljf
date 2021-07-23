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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @author Administrator
 * @version 1.0
 * @date 2014年2月16日
 */
public class AsyncTimeClientHandler implements
        CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {

    private AsynchronousSocketChannel client;
    private String host;
    private int port;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            //创建异步socket通道
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        latch = new CountDownLatch(1);
        //A attachment，CompletionHandler，两个参数都使用AsyncTimeClientHandler类本身，因为它实现了CompletionHandler接口”
        client.connect(new InetSocketAddress(host, port), this, this);
        try {
            //“防止异步操作没有执行完成线程就退出”
            latch.await();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        //异步写
        client.write(writeBuffer, writeBuffer,
                new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        if (buffer.hasRemaining()) {
                            //“如果发送缓冲区中仍有尚未发送的字节，将继续异步发送”
                            //“如果已经发送完成，则执行异步读取操作”
                            client.write(buffer, buffer, this);
                        } else {
                            //执行异步读取操作
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                            //“read操作是异步的，所以我们通过内部匿名类实现CompletionHandle
                            client.read(
                                    readBuffer,
                                    readBuffer,
                                    new CompletionHandler<Integer, ByteBuffer>() {
                                        @Override
                                        public void completed(Integer result,
                                                              ByteBuffer buffer) {
                                            buffer.flip();
                                            byte[] bytes = new byte[buffer
                                                    .remaining()];
                                            buffer.get(bytes);
                                            String body;
                                            try {
                                                body = new String(bytes,
                                                        "UTF-8");
                                                System.out.println("Now is : "
                                                        + body);
                                                latch.countDown();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void failed(Throwable exc,
                                                           ByteBuffer attachment) {
                                            try {
                                                client.close();
                                                latch.countDown();
                                            } catch (IOException e) {
                                                // ingnore on close
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            client.close();
                            latch.countDown();
                        } catch (IOException e) {
                            // ingnore on close
                        }
                    }
                });
    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        exc.printStackTrace();
        try {
            client.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
