package io.github.ztmark.start;

import java.nio.charset.StandardCharsets;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 * 拆包 粘包 Server
 *
 * @Author: Mark
 * @Date : 2018-12-04
 */
public class PackageMergeServer {


    public static void main(String[] args) {
        final NioEventLoopGroup boss = new NioEventLoopGroup(1);
        final NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ch.pipeline()
//                       .addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE)) // 以行分割
//                       .addLast(new FixedLengthFrameDecoder(100)) // 以长度分割
                       .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2, 4)) // 已长度字段值分割
                       .addLast(new PongHandler());
                 }
             });

            final ChannelFuture future = b.bind(8680).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private static class PongHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf buf = (ByteBuf) msg;
            buf.skipBytes(6);
            System.out.println("-----> " + buf.toString(StandardCharsets.UTF_8));
        }
    }

}
