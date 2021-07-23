/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ljf.netty.frame.fault;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author lilinfeng
 * @version 1.0
 * @date 2014年2月14日
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    private int counter;

    /**
     * 读取客户端发送的消息
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
//        String body = new String(req, "UTF-8");
        String body = new String(req, "UTF-8").substring(0, req.length
                - System.getProperty("line.separator").length());
        System.out.println("The time server receive order : " + body
                + " ; the counter is : " + ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(
                System.currentTimeMillis()).toString() : "BAD ORDER";
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
        //“异步发送应答消息给客户端”
//        ctx.write(resp);
    }

    /**
     * “将消息发送队列中的消息写入到SocketChannel中发送给对方
     *
     * @param ctx
     * @throws Exception
     */
   /* @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        *//*
         *“从性能角度考虑，为了防止频繁地唤醒Selector进行消息发送，
         * Netty的write方法并不直接将消息写入SocketChannel中，
         * 调用write方法只是把待发送的消息放到发送缓冲数组中，再通过调用flush方法，将发送缓冲区中的消息全部写到SocketChannel中”
         *//*
        ctx.flush();
    }*/

    /**
     * “当发生异常时，关闭ChannelHandlerContext，释放和ChannelHandlerContext相关联的句柄等资源”
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
