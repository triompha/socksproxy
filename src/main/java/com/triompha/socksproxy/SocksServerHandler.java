package com.triompha.socksproxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socks.SocksAuthRequest;
import io.netty.handler.codec.socks.SocksAuthRequestDecoder;
import io.netty.handler.codec.socks.SocksAuthResponse;
import io.netty.handler.codec.socks.SocksAuthScheme;
import io.netty.handler.codec.socks.SocksAuthStatus;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdRequestDecoder;
import io.netty.handler.codec.socks.SocksCmdType;
import io.netty.handler.codec.socks.SocksInitResponse;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.util.internal.StringUtil;


@ChannelHandler.Sharable
public final class SocksServerHandler extends SimpleChannelInboundHandler<SocksRequest> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SocksRequest socksRequest) throws Exception {
        switch (socksRequest.requestType()) {
            case INIT: {
                // auth support example
                if(StringUtil.isNullOrEmpty(Config.AUTH)){
                  ctx.pipeline().addFirst(new SocksCmdRequestDecoder());
                  ctx.write(new SocksInitResponse(SocksAuthScheme.NO_AUTH));
                }else{
                    ctx.pipeline().addFirst(new SocksAuthRequestDecoder());
                    ctx.write(new SocksInitResponse(SocksAuthScheme.AUTH_PASSWORD));
                }
                break;
            }
            case AUTH:
                //校验密码是否正确
                if("password".equals(Config.AUTH)){
                    SocksAuthRequest authRequest = (SocksAuthRequest) socksRequest;
                    String savedPwd = (String) Config.passwordInfo.get(authRequest.username());
                    if(savedPwd==null || !savedPwd.equals(authRequest.password())){
                        ctx.write(new SocksAuthResponse(SocksAuthStatus.FAILURE));
                        break;
                    }
                }
                ctx.pipeline().addFirst(new SocksCmdRequestDecoder());
                ctx.write(new SocksAuthResponse(SocksAuthStatus.SUCCESS));
                break;
            case CMD:
                SocksCmdRequest req = (SocksCmdRequest) socksRequest;
                if (req.cmdType() == SocksCmdType.CONNECT) {
                    ctx.pipeline().addLast(new SocksServerConnectHandler());
                    ctx.pipeline().remove(this);
                    ctx.fireChannelRead(socksRequest);
                } else {
                    ctx.close();
                }
                break;
            case UNKNOWN:
                ctx.close();
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        throwable.printStackTrace();
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}