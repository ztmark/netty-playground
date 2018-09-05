package io.github.ztmark;

import java.nio.charset.StandardCharsets;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Author: Mark
 * Date  : 2018/6/4
 */
public class HelloWorld {

    public static void main(String[] args) {
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .handler(new LoggingHandler(LogLevel.INFO))
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             ch.pipeline().addLast(new HttpServerCodec(), new SimpleChannelInboundHandler<HttpObject>() {

                                 @Override
                                 protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                     if (msg instanceof LastHttpContent) {
                                         final ByteBuf buf = Unpooled.copiedBuffer("Hello World", StandardCharsets.UTF_8);
                                         final DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
                                         response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                                         response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
                                         ctx.write(response);
                                     }
                                 }

                                 @Override
                                 public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                     ctx.flush();
                                 }
                             });
                         }
                     })
                     .childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.bind(8680).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
