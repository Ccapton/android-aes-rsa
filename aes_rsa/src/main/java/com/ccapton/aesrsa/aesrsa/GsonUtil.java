package com.ccapton.aesrsa.aesrsa;

import com.google.gson.Gson;

public class GsonUtil {
    public static String to_String(Object o){
        return new Gson().toJson(o);
    }
    public static Object to_Json(String s,Class type){
        return new Gson().fromJson(s,type);
    }
}
