package com.log.collect.tools.filter.wrapper;


import com.log.collect.tools.filter.wrapper.request.CustomHttpServletRequestWrapper;
import com.log.collect.tools.filter.wrapper.response.CustomHttpServletResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lij
 * @description: TODO
 * @date 2023/2/22 9:15
 */
public class HttpWrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            CustomHttpServletRequestWrapper customHttpServletRequestWrapper = new CustomHttpServletRequestWrapper((HttpServletRequest) request);
            CustomHttpServletResponseWrapper customHttpServletResponseWrapper= new CustomHttpServletResponseWrapper((HttpServletResponse) response);
            chain.doFilter(customHttpServletRequestWrapper,customHttpServletResponseWrapper);
        }else {
            chain.doFilter(request,response);
        }

    }
}
