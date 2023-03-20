package com.log.collect.tools.log.log4j;

import com.log.collect.tools.configure.LogConfigProperties;
import com.log.collect.tools.spring.SpringBeanContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lij
 * @description: TODO
 * @date 2023/2/24 11:44
 * */


public class Log4jConfiguration {


    public static  Logger Collect_LOGGER=null;

    static {
        init();
        Collect_LOGGER=LoggerFactory.getLogger("LOG_COLLECT_LOGGER");
    }
    static void init(){
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();




        LogConfigProperties logConfigProperties=SpringBeanContext.getBean(LogConfigProperties.class);

        //"%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"
        final String pattern = logConfigProperties.getLayoutPattern() == null ? "%d{yyyy-MM-dd HH:mm:ss.SSS} - %msg%n" : logConfigProperties.getLayoutPattern();
        Layout<String> layout = PatternLayout.newBuilder()
                .withPattern(pattern)
                .build();

        Filter filter=ThresholdFilter.createFilter(Level.INFO, Filter.Result.ACCEPT, Filter.Result.DENY);
        TimeBasedTriggeringPolicy timeBasedTriggeringPolicy=TimeBasedTriggeringPolicy.newBuilder()
                .withInterval(1)
                .withModulate(true)
                .build();
        SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy=SizeBasedTriggeringPolicy.createPolicy("100 MB");
        CompositeTriggeringPolicy compositeTriggeringPolicy=CompositeTriggeringPolicy.createPolicy(timeBasedTriggeringPolicy,sizeBasedTriggeringPolicy);
        DefaultRolloverStrategy defaultRolloverStrategy= DefaultRolloverStrategy.newBuilder()
                .withFileIndex("nomax")
                .build();
        Appender appender=RollingFileAppender.newBuilder()
                .withFileName(logConfigProperties.getFileName())
                .withFilePattern(logConfigProperties.getFileName()+"-%d{yyyy-MM-dd}-%i.zip")
                .setLayout(layout)
                .setName("log_collect_appender")
                .setFilter(filter)
                .withPolicy(compositeTriggeringPolicy)
                .withStrategy(defaultRolloverStrategy)
                .build();


//        Appender appender = FileAppender.createAppender(logCollectProperties.getFileName(), "false", "false", "File", "true",
//                "false", "false", "4000", layout, null, "false", null, config);
        //appender.start();
        config.addAppender(appender);
        AppenderRef ref = AppenderRef.createAppenderRef("log_collect_appender", null, null);
        AppenderRef[] refs = new AppenderRef[] {ref};
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.INFO, "LOG_COLLECT_LOGGER",
                "true", refs, null, config, null );
        loggerConfig.addAppender(appender, null, null);
        config.addLogger("LOG_COLLECT_LOGGER", loggerConfig);
        ctx.updateLoggers();
    }

}
