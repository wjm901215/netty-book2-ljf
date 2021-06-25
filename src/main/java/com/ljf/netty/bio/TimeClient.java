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
package com.ljf.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.IntStream;

/**
 * 时间客户端
 * 读和写操作都是同步阻塞的，阻塞的时间取决于对方I/O线程的处理速度和网络I/O的传输速度
 * @author lilinfeng
 * @version 1.0
 * @date 2014年2月14日
 */
public class TimeClient {

    /**
     * @param args
     */
    public static void main(String[] args) {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            System.out.println("调用第" + i + "次");
            socketClient();
        });
    }

    private static void socketClient() {
        int port = 8080;
        try (Socket socket = new Socket("127.0.0.1", port);
             InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
             BufferedReader in = new BufferedReader(inputStreamReader);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            //发送消息至服务端
            out.println("QUERY TIME ORDER");
            System.out.println("Send order 2 server succeed.");
            //阻塞，等待服务端响应结果
            String resp = in.readLine();
            System.out.println("Now is : " + resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
