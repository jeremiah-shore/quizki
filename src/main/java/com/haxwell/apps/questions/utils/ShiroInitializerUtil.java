package com.haxwell.apps.questions.utils;

public class ShiroInitializerUtil {
    private String serverName;

    public ShiroInitializerUtil() {
        this.serverName = System.getenv("SHIRO_SERVER_NAME");
    }

    public String getServerName() {
        return serverName;
    }
}
