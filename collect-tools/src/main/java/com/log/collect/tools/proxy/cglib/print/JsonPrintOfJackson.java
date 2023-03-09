package com.log.collect.tools.proxy.cglib.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lij
 * @description: TODO
 * @date 2023/3/9 9:18
 */
public final class JsonPrintOfJackson {

    private static  ObjectMapper printer=new ObjectMapper();
    public static String print(Object t){
        try {
            return printer.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };

    public static void setPrinter(ObjectMapper objectMapper){
        printer=objectMapper;
    }

}
