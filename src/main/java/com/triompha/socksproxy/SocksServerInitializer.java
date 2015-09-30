package com.triompha.socksproxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socks.SocksInitRequestDecoder;
import io.netty.handler.codec.socks.SocksMessageEncoder;

public final class SocksServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SocksMessageEncoder socksMessageEncoder = new SocksMessageEncoder();
    private final SocksServerHandler socksServerHandler = new SocksServerHandler();

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new SocksInitRequestDecoder());
        p.addLast(socksMessageEncoder);
        p.addLast(socksServerHandler);
    }
}