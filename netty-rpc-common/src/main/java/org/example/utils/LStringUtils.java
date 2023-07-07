package org.example.utils;

import org.example.logger.Debug;

public enum LStringUtils {
    Instance;

    public boolean isNullOrEmpty(String msg){
        return msg == null || msg.isEmpty();
    }

    public String[] strSplit(String data,String regex){
        String[] strArray = data.split(regex);
        return strArray;
    }
}
