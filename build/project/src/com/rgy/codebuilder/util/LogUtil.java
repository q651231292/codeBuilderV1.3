package com.rgy.codebuilder.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class LogUtil {

    static {
        // 读取配置文件
        ClassLoader cl = LogUtil.class.getClassLoader();
        InputStream inputStream = null;
        String logProperties = "mylog.properties";
		if (cl != null) {
            inputStream = cl.getResourceAsStream(logProperties);
        } else {
            inputStream = ClassLoader.getSystemResourceAsStream(logProperties);
        }
        LogManager logManager = LogManager.getLogManager();
        try {
            // 重新初始化日志属性并重新读取日志配置。
            logManager.readConfiguration(inputStream);
        } catch (SecurityException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    public static Logger getLogger() {
        Logger log = Logger.getLogger("codeBuilderLogger");
        log.setLevel(Level.ALL);
        return log;
    }

}