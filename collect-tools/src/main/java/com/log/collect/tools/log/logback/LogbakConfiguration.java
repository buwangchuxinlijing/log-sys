package com.log.collect.tools.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import com.log.collect.tools.configure.LogConfigProperties;
import com.log.collect.tools.spring.SpringBeanContext;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author lij
 * @description: TODO
 * @date 2023/2/27 16:09
 */
public class LogbakConfiguration {
    public static  org.slf4j.Logger Collect_LOGGER;

    static {
        configure();
        Collect_LOGGER = LoggerFactory.getLogger("LOG_COLLECT_LOGGER");
    }


    /**
     * 參考了BasicConfigurator
     */
    public static void configure() {
        LogConfigProperties logConfigProperties= SpringBeanContext.getBean(LogConfigProperties.class);

        LoggerContext lc= (LoggerContext) LoggerFactory.getILoggerFactory();

        RollingFileAppender<ILoggingEvent> ra=new RollingFileAppender<>();
        ra.setName("log_collect_appender");
        ra.setFile(logConfigProperties.getFileName());

        SizeAndTimeBasedRollingPolicy rollingPolicy=new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(lc);
        rollingPolicy.setFileNamePattern(logConfigProperties.getFileName()+"-%d{yyyy-MM-dd}-%i.zip");
        FileSize fileSize= FileSize.valueOf("100 MB");
        rollingPolicy.setMaxFileSize(fileSize);
        rollingPolicy.setMaxHistory(14);
        rollingPolicy.setParent(ra);
        rollingPolicy.start();

        ra.setRollingPolicy(rollingPolicy);

        PatternLayoutEncoder patternLayoutEncoder=new PatternLayoutEncoder();
        patternLayoutEncoder.setCharset(StandardCharsets.UTF_8);
        //%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - GG %msg%n"
        final String pattern = logConfigProperties.getLayoutPattern() == null ? "%d{yyyy-MM-dd HH:mm:ss.SSS} - %msg%n" : logConfigProperties.getLayoutPattern();
        patternLayoutEncoder.setPattern(pattern);
        patternLayoutEncoder.setContext(lc);
        patternLayoutEncoder.start();

        ra.setEncoder(patternLayoutEncoder);

        LevelFilter levelFilter=new LevelFilter();
        levelFilter.setLevel(Level.INFO);
        levelFilter.setOnMatch(FilterReply.ACCEPT);
        levelFilter.setOnMismatch(FilterReply.DENY);
        levelFilter.setContext(lc);
        levelFilter.start();

        ra.addFilter(levelFilter);



        ra.setContext(lc);
        ra.start();


        Logger logger = lc.getLogger("LOG_COLLECT_LOGGER");
        logger.addAppender(ra);
        logger.setAdditive(false);
        logger.setLevel(Level.INFO);
    }
}
