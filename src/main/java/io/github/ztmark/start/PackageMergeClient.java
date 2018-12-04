package io.github.ztmark.start;

import java.nio.charset.StandardCharsets;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 *
 * 拆包 粘包 client
 *
 * @Author: Mark
 * @Date : 2018-12-04
 */
public class PackageMergeClient {


    public static void main(String[] args) {
        final NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                     .channel(NioSocketChannel.class)
                     .option(ChannelOption.TCP_NODELAY, true)
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) {
                             ch.pipeline().addLast(new PingHandler());
                         }
                     });
            final ChannelFuture future = bootstrap.connect("127.0.01", 8680).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }


    private static class PingHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            for (int i = 0; i < 1000; i++) {
                ctx.writeAndFlush(content(ctx));
            }
        }

        private ByteBuf content(ChannelHandlerContext ctx) {
            final ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeByte(0x12);
            buffer.writeByte(0x01);
            buffer.writeInt("任何不能杀死你的，都会使你更强大.".getBytes(StandardCharsets.UTF_8).length);
            final byte[] bytes = "任何不能杀死你的，都会使你更强大.".getBytes(StandardCharsets.UTF_8);
            buffer.writeBytes(bytes);
            return buffer;
        }
    }

}
