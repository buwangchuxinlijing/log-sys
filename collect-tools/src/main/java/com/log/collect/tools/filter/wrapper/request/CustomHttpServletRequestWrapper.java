package com.log.collect.tools.filter.wrapper.request;



import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private  byte[] body;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        InputStream inputStream=request.getInputStream();
        try (
                BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream);
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ) {
            byte[] bytes = new byte[1024];
            int len;
            while ( (len =bufferedInputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes,0,len);
            }
            this.body = byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CustomServletInputStream(new ByteArrayInputStream(this.body));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.body)));
    }

    private class CustomServletInputStream extends ServletInputStream{
        private InputStream in;
        public CustomServletInputStream(InputStream in){
            this.in=in;
        }

        @Override
        public int read() throws IOException {
            return this.in.read();
        }

        @Override
        public boolean isFinished() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReady() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.in.close();
        }
    }
}
