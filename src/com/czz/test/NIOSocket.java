package com.czz.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author czz
 * @version 1.0
 * @date 2020/9/28 20:59
 */
public class NIOSocket {
    public static void main(String[] args) throws IOException {
        server();
    }

    /**
     * 服务端
     * @throws IOException
     */
    public static void server() throws IOException {
        //创建服务端socket通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8899));

        //创建文件通道
        FileChannel fileChannel = FileChannel.open(Paths.get("D:\\敦刻尔克.mp4"),StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        //开启接收客户端通道
        SocketChannel accept = serverSocketChannel.accept();

        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);

        //接收客户端数据
        while (accept.read(byteBuffer) != -1){
            //切换读模式
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //关闭流
        fileChannel.close();
        serverSocketChannel.close();
    }


}
class ClientSet{
    public static void main(String[] args) throws IOException {
        client();
    }
    /**
     * 客户端
     * @throws IOException
     */
    public static void client() throws IOException {
        //创建socket通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8899));
        //创建文件通道
        FileChannel fileChannel = FileChannel.open(Paths.get("E:\\电影\\敦刻尔克.rmvb"), StandardOpenOption.READ);

        //设置缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);

        while (fileChannel.read(byteBuffer) != -1){
            //切换为读模式
            byteBuffer.flip();
            //向Socket通道中写入
            socketChannel.write(byteBuffer);
            //清除缓冲区数据
            byteBuffer.clear();
        }
        //关闭流
        fileChannel.close();
        socketChannel.close();
    }


}
