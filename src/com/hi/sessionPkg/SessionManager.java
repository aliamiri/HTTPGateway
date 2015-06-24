package com.hi.sessionPkg;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hi.servicePkg.IService;
import com.hi.servicePkg.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by a.akhondian on 6/14/2015.
 */
public class SessionManager {

    public static ConcurrentHashMap<Integer, String> serviceNames = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public Response defResp = new Response("1", "0", "yeki az gozinehaye zir ra entekhab konid:\r\n 1.kharid sharge \r\n 2.tanzimat");


    LoadingCache<String, Session> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS).removalListener(event -> sessions.remove(event.getKey()))
            .build(
                    new CacheLoader<String, Session>() {
                        public Session load(String key) {
                            try {
                                return sessions.get(key);
                            } catch (Exception ex) {
                                return null;
                            }
                        }
                    });

    public void removeSession(String sessionId) {
        cache.invalidate(sessionId);
    }

    public Response addSession(String mobileNumber, String input) {
        Response response = null;
        Session session = new Session();
        try {
            session.inputs = parseInput(input);
            if (session.inputs.size() > 1) {
                session.inputs.remove(0);
                IService instance = (IService) Class.forName(serviceNames.get(session.inputs.remove(0))).newInstance();
                instance.init(session.lang,session.mobileNo);
                session.service = instance;
                response = session.service.processCode(session.inputs);
            } else {
                response = defResp;
            }
            session.mobileNo = mobileNumber;
            cache.put(mobileNumber, session);
            sessions.put(mobileNumber, session);
        } catch (Exception e) {
            removeSession(session.mobileNo);
        }
        return response;
    }

    private List<String> parseInput(String input) {
        input = input.replace("#", "");
        String[] split = input.split("\\*");
        List<String> ret = new ArrayList<>();
        for (String s : split)
            if (s != null && !s.equals(""))
                ret.add(s);
        return ret;
    }

    public Response updateSession(Session session, String input) {
        try {
            if (session.service == null)
                session.service = (IService) Class.forName(serviceNames.get(session.inputs.remove(0))).newInstance();
            session.inputs = parseInput(input);
            Response response = session.service.processCode(session.inputs);
            session.inputs = null;
            return response;
        } catch (Exception e) {
            removeSession(session.mobileNo);
        }
        return defResp;
    }

    public Session getSession(String key) {
        try {
            return cache.getIfPresent(key);
        } catch (Exception e) {
            return null;
        }
    }
}
