package com.log.collect.tools.filter.wrapper.response;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * @author lij
 * @description: TODO
 * @date 2023/3/6 9:03
 */
public class CustomHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    ServletOutputStream outputStream;

    PrintWriter printWriter;
    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the {@link HttpServletResponse} to be wrapped.
     * @throws IllegalArgumentException if the response is null
     */
    public CustomHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream==null){
            outputStream=new CustomServletOutputStream(super.getOutputStream(),buffer);
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (printWriter==null){
            printWriter=new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
        }

        return printWriter;
    }

    // 重写flushBuffer方法，将缓冲区中的数据输出到HTTP响应中
    @Override
    public void flushBuffer() throws IOException {
        if (printWriter != null) {
            printWriter.flush();
        } else if (outputStream != null) {
            outputStream.flush();
        }
        super.flushBuffer();
    }

    private class CustomServletOutputStream extends ServletOutputStream{
        private OutputStream outputStream;
        private ByteArrayOutputStream byteArrayOutputStream;
        public CustomServletOutputStream(OutputStream outputStream,ByteArrayOutputStream byteArrayOutputStream){
            this.outputStream=outputStream;
            this.byteArrayOutputStream=byteArrayOutputStream;
        }

        @Override
        public boolean isReady() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            byteArrayOutputStream.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            outputStream.write(b);
            byteArrayOutputStream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            outputStream.write(b, off, len);
            byteArrayOutputStream.write(b, off, len);
        }
    }
    // 重写getResponse方法，返回一个自定义的HTTP响应对象
    public String getResponseData() throws IOException {
        flushBuffer();
        return buffer.toString(getCharacterEncoding());
    }
}
