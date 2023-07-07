package org.example.logger;

import org.apache.log4j.Logger;

public class Debug {
    private static final Logger loggerInfo = Logger.getLogger("LOGGER");
    private static final Logger loggerDebug = Logger.getLogger("LOGGER");
    private static final Logger loggerError = Logger.getLogger("LOGGER");
    private static final Logger loggerWarn = Logger.getLogger("LOGGER");


    public enum  LEVEL{
        INFO,
        DEBUG,
        WARNING,
        ERROR,
        ;
    }

    public static void debug(String message){
        output(message, LEVEL.DEBUG);
    }
    public static void info(String message){
        output(message, LEVEL.INFO);
    }

    public static void warn(String message){
        output(message, LEVEL.WARNING);
    }

    public static void err(String message,Exception e){
        output(message, LEVEL.ERROR);
        exceptionOutput(e, LEVEL.ERROR);
    }

    public static void err(String message){
        output(message, LEVEL.ERROR);
    }

    private static void output(String message,LEVEL tag){
        switch (tag){
            case INFO:
                loggerInfo.info(message);
                break;
            case DEBUG:
                loggerDebug.debug(message);
                break;
            case WARNING:
                loggerWarn.warn(message);
                break;
            case ERROR:
                loggerError.error(message);
                break;
        }
    }

    private static void exceptionOutput(Exception e,LEVEL tag){
        if(e == null){
            return;
        }

        output(e.toString(),tag);
        for (StackTraceElement o : e.getStackTrace()) {
            output("[STACK-TRACE]------> " + o.toString(),tag);
        }
    }

}
