package com.czz.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author czz
 * @version 1.0
 * @date 2020/9/28 21:39
 */
public class NIOMapStore {
    public static void main(String[] args) throws IOException {
        test();
    }

    public static void test() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("E:\\电影\\敦刻尔克.rmvb"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("E:\\敦刻尔克.mp4"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        //创建通道，通道容量都为文件读入通道到小相同
        MappedByteBuffer in = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer out = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        //创建临时存储
        byte[] bytes = new byte[in.limit()];
        //读入数据
        in.get(bytes);
        //写出数据
        out.put(bytes);

        //关闭流
        inChannel.close();
        outChannel.close();
    }
}
