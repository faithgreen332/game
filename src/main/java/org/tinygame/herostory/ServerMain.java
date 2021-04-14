package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;

import java.net.InetSocketAddress;

/**
 * Author: ljf
 * CreatedAt: 2021/4/12 上午9:06
 */
public class ServerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        // 日志配置拉进来
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        // 初始化命令处理器工厂
        CmdHandlerFactory.init();

        // 初始化 msgDecoder 工厂构建器
        GameMsgRecognizer.init();

        // 初始化 mysql 连接
        MySqlSessionFactory.init();

        // netty
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpServerCodec(),
                                new HttpObjectAggregator(65535),
                                new WebSocketServerProtocolHandler("/websocket"),
                                new GameMsgDecoder(),
                                new GameMsgEncoder(),
                                new GameMessageHandler());
                    }
                });

        b.option(ChannelOption.SO_BACKLOG, 128);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture f = b.bind(new InetSocketAddress(8080)).sync();
            if (f.isSuccess()) {
                LOGGER.info("服务器启动成功");
            }
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
