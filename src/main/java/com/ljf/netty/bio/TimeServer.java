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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 阻塞IO
 * 读和写操作都是同步阻塞的，阻塞的时间取决于对方I/O线程的处理速度和网络I/O的传输速度
 * @author lilinfeng
 * @version 1.0
 * @date 2014年2月14日
 */
public class TimeServer {
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }
    /**
     * 采用BIO通信模型的服务端，通常由一个独立的Acceptor线程负责监听客户端的连接，
     * 它接收到客户端连接请求之后为每个客户端创建一个新的线程进行链路处理，
     * 处理完成之后，通过输出流返回应答给客户端，线程销毁
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {

            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }

        }
        //try关闭流_使用try with resource 关闭流
        try(ServerSocket server =new ServerSocket(port)) {
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            while (true) {
                //“监听客户端的连接，如果没有客户端接入，则主线程阻塞在accept操作，”
                socket = server.accept();
                //“当有新的客户端接入的时候，执行”
                new Thread(new TimeServerHandler(socket),"TimeServerThread-"+ nextThreadNum()).start();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
