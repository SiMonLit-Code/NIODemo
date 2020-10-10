package com.czz.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

/**
 * @author : czz
 * @version : 1.0.0
 * @create : 2020-09-29 10:49:00
 * @description :
 */
public class NIOSelector {
    public static void main(String[] args) throws IOException {
        new NIOSelector.Client().client();

    }
    static class Client{
        public void client() throws IOException {
            //创建socket通道
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8898));

            FileChannel fileChannel = FileChannel.open(Paths.get("D:\\relation.xlsx"), StandardOpenOption.READ, StandardOpenOption.WRITE);

            ByteBuffer buffer = ByteBuffer.allocate(1024*1024);

            while (fileChannel.read(buffer) != -1){
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
            }

            fileChannel.close();
            socketChannel.close();
        }
    }
}

class Server {
    public static void main(String[] args) throws IOException {
        server();
    }
    public static void server() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8899));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();

        FileChannel fileChannel = FileChannel.open(Paths.get("D:\\hello.txt"),StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        //将serverSocketChannel注册进selector，
        //SelectionKey.OP_ACCEPT = 4  可以连接状态
        //SelectionKey.OP_CONNECT = 3 已连接状态
        //SelectionKet.OP_WRITE = 2 可写出状态
        //SelectionKeyOP_READ = 1 可读入状态

        //就绪状态
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //1、轮训地获取选择器上已“就绪”的事件--->只要select()>0，说明已就绪
        while (selector.select()>0){
            //2、获取当前选择器所有注册的“选择键”(已就绪的监听事件)
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            //3、获取已“就绪”的事件，(不同的事件做不同的事)
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if (key.isAcceptable()){
                    //3.1获取客户端的链接
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //3.2换成非阻塞状态
                    socketChannel.configureBlocking(false);
                    //3.3注册到选择器上-->拿到客户端的连接为了读取通道的数据(监听读就绪事件)
                    socketChannel.register(selector, SelectionKey.OP_READ);

                    //4、获取已“已连接状态”的事件
                }else if (key.isReadable()){
                    //4.1获取当前选择器读就绪状态的通道
                    SocketChannel channel = (SocketChannel)key.channel();
                    //4.2创建缓冲区
                    ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
                    //4.3读取
                    while (channel.read(buffer) != -1){
                        buffer.flip();
                        fileChannel.write(buffer);
                        buffer.clear();
                    }
                }
                iterator.remove();
            }
        }
    }
}
