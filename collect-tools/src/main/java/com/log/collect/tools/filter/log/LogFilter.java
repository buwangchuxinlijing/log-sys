package com.log.collect.tools.filter.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.collect.tools.filter.wrapper.response.CustomHttpServletResponseWrapper;
import com.log.collect.tools.log.LogAutoConfig;
import com.log.collect.tools.user.LogUserInfoObtain;
import com.log.collect.tools.user.UserModel;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lij
 * @description: TODO
 * @date 2023/3/1 16:05
 */
public class LogFilter implements Filter {

    private LogUserInfoObtain logUserInfo;


    private ObjectMapper objectMapper = new ObjectMapper();

    private AntPathMatcher pathMatcher = new AntPathMatcher();


    public LogFilter(LogUserInfoObtain logUserInfo) {
        this.logUserInfo = logUserInfo;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UserModel userModel = null;
        String requestURL = null;
        String uuid = null;
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            userModel = logUserInfo.obtain(request);
            Enumeration<String> enumeration = httpServletRequest.getHeaderNames();
            Map<String, String> headMap = new HashMap<>();
            while (enumeration.hasMoreElements()) {
                String head = enumeration.nextElement();
                Enumeration<String> headerValues = httpServletRequest.getHeaders(head);
                List<String> values = new ArrayList<>();
                while (headerValues.hasMoreElements()) {
                    values.add(headerValues.nextElement());
                }
                headMap.put(head, values.stream().collect(Collectors.joining(";")));
            }
            StringJoiner sj = new StringJoiner(" - ");

            requestURL = httpServletRequest.getRequestURL().toString();
            String query=httpServletRequest.getQueryString();
            if (query!=null){
                requestURL+="?"+query;
            }
            //请求行 url
            sj.add(requestURL );
            //请求头
            sj.add(objectMapper.writeValueAsString(headMap));
            //请求体
            sj.add(request.getReader().lines().collect(Collectors.joining()));
            //用户信息
            sj.add(objectMapper.writeValueAsString(userModel));
            //请求id
            uuid = UUID.randomUUID().toString();
            sj.add(uuid);
            LogAutoConfig.COLLECT_LOGGER.info(sj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        chain.doFilter(request, response);
        try {
            if (response instanceof CustomHttpServletResponseWrapper) {
                CustomHttpServletResponseWrapper responseWrapper = (CustomHttpServletResponseWrapper) response;
                responseWrapper.getResponseData();
                StringJoiner sj = new StringJoiner(" - ");
                //响应行: url + 状态码
                sj.add(requestURL + " " + responseWrapper.getStatus());
                Collection<String> collection = responseWrapper.getHeaderNames();
                Map<String, String> responseHead = collection.stream().collect(Collectors.toMap(name -> name,
                        name -> (responseWrapper.getHeaders(name).stream().collect(Collectors.joining(";"))),
                        (u, v) -> u + ";" + v,
                        HashMap::new
                        )
                );
                //响应头
                sj.add(objectMapper.writeValueAsString(responseHead));
                //响应体
                sj.add(responseWrapper.getResponseData());
                //用户信息
                sj.add(objectMapper.writeValueAsString(userModel));
                //请求id
                sj.add(uuid);

                LogAutoConfig.COLLECT_LOGGER.info(sj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
