package io.github.ztmark.start;

import java.util.Date;
import java.util.List;

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
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

/**
 * Author: Mark
 * Date  : 2017/12/26
 */
public class TimeClient {


    public static void main(String[] args) throws InterruptedException {
        final NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            final Bootstrap b = new Bootstrap();
            b.group(loopGroup)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 protected void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new TimeDecoder(), new TimeClientHandler());
                 }
             })
             .option(ChannelOption.SO_KEEPALIVE, true);
            final ChannelFuture f = b.connect("127.0.0.1", 8680).sync();
            f.channel().closeFuture().sync();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }

    static class TimeDecoder extends ByteToMessageDecoder {

        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (in.readableBytes() < 4) {
                return;
            }
            out.add(in.readBytes(4));
        }
    }

    static class TimeDecoder1 extends ReplayingDecoder<Void> {

        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            out.add(in.readBytes(4));
        }
    }

    static class TimeClientHandler1 extends ChannelInboundHandlerAdapter {

        private ByteBuf buf;

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            buf = ctx.alloc().buffer(4);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            buf.release();
            buf = null;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf b = (ByteBuf) msg;
            buf.writeBytes(b);
            if (buf.readableBytes() >= 4) {
                long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
                System.out.println(new Date(currentTimeMillis));
                ctx.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    static class TimeClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            try {
                final long millis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
                System.out.println(new Date(millis));
            } finally {
                buf.release();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

}
