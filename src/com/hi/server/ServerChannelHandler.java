package com.hi.server;

import com.hi.services.Response;
import com.hi.sessions.Session;
import com.hi.sessions.SessionManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class ServerChannelHandler extends ChannelHandlerAdapter {
    private static final byte[] CONTENT = {};
    private static SessionManager sessionManager = new SessionManager();


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            if (HttpHeaderUtil.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            QueryStringDecoder queryDecoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> queryParams = queryDecoder.parameters();

            Session session = sessionManager.getSession(queryParams.get("MSISDN").get(0));
            Response sessionResponse;
            if (session != null)
                sessionResponse = sessionManager.updateSession(session, queryParams.get("input").get(0));
            else
                sessionResponse = sessionManager.addSession(queryParams.get("MSISDN").get(0), queryParams.get("input").get(0));

            boolean keepAlive = HttpHeaderUtil.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(sessionResponse.getResponseText().getBytes()));
            response.headers().set(EXPIRES, "-1");
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}