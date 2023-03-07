package com.log.collect.tools.configure;

/**
 * @author lij
 * @description: TODO
 * @date 2023/2/20 18:26
 */

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties("log.collect")
public class LogConfigProperties {
    private String open;
    private List<String> urls;
    private String fileName;
    private String layoutPattern;


    public String getLayoutPattern() {
        return layoutPattern;
    }

    public void setLayoutPattern(String layoutPattern) {
        this.layoutPattern = layoutPattern;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }


}
