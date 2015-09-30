package com.triompha.socksproxy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class Config {

    public static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static JSONObject configJson;

    static {
        
        //默认为工程根目录的config.conf，  可以设置系统环境变量来更改。
        String configPath = System.getProperty("config.path", "") + "/config.conf";
        String configContent = "";
        try {
            
            if(System.getProperty("config.path", "").equals("")){
                configPath =Config.class.getResource(configPath).getPath();
            }
            
            logger.info("config path:" + configPath);
            configContent = FileUtils.readFileToString(new File(configPath),"UTF-8");
            
        } catch (IOException e) {
            logger.error("load config error!", e);
            System.exit(1);
        }
        configJson = JSON.parseObject(configContent);
        PORT = configJson.getInteger("port");
        AUTH = configJson.getString("auth");
        passwordInfo = configJson.getJSONObject("password")==null?(new HashMap<String, Object>()):(configJson.getJSONObject("password"));
    }


    public static void loadPassword() {}


    public static int PORT;

    public static String AUTH;

    public static Map<String, Object> passwordInfo;


}
