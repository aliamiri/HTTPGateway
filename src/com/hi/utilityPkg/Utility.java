package com.hi.utilityPkg;

import com.google.common.collect.Maps;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by a.akhondian on 6/23/2015.
 */
public class Utility {

    private static Map<String, String> persianMap;
    private static Map<String, String> englishMap;
    private static Map<String, String> finglishMap;
    private static Utility _instance;
    private static Object lock = new Object();

    private Utility() {
        initialize();
    }

    public static Utility instance() {
        if (_instance != null)
            return _instance;
        synchronized (lock) {
            if (_instance == null)
                _instance = new Utility();
        }
        return _instance;
    }

    private void initialize() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("fa_messages.properties"));
            persianMap = Maps.fromProperties(properties);
            properties.load(new FileInputStream("en_messages.properties"));
            englishMap = Maps.fromProperties(properties);
            properties.load(new FileInputStream("fi_messages.properties"));
            finglishMap = Maps.fromProperties(properties);
        } catch (IOException e) {
            //TODO log
        }
    }

    public String getMessage(String inputStr, List<String> replacesStrs, int lang) {
        String retVal = "";
        switch (lang) {
            case 0:
                retVal = persianMap.get(inputStr);
                break;
            case 1:
                retVal = englishMap.get(inputStr);
                break;
            case 2:
                retVal = finglishMap.get(inputStr);
                break;
        }
        while (replacesStrs != null && replacesStrs.size() > 0)
            retVal = inputStr.replaceFirst("&?&", replacesStrs.get(0));
        return retVal;
    }
}
