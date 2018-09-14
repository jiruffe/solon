package org.noear.solonboot.jetty;

import org.noear.solonboot.XMap;
import org.noear.solonboot.XUtil;
import org.noear.solonboot.protocol.XContext;
import org.noear.solonboot.protocol.XHeader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JtHttpContext extends XContext{
    private HttpServletRequest _request;
    private HttpServletResponse _response;

    public JtHttpContext(HttpServletRequest request, HttpServletResponse response) {
        _request = request;
        _response = response;
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String ip() {
        return _request.getRemoteAddr();
    }

    @Override
    public boolean isMultipart() {
        return header(XHeader.CONTENT_TYPE,"").toLowerCase().contains("multipart/");
    }

    @Override
    public String method() {
        return _request.getMethod();
    }

    @Override
    public String protocol() {
        return _request.getProtocol();
    }

    @Override
    public URI uri() {
        if(_uri == null) {
            _uri = URI.create(_request.getRequestURI());
        }

        return _uri;
    }
    private URI _uri;

    @Override
    public String path() {
        return _request.getPathInfo();
    }

    @Override
    public String userAgent() {
        return header(XHeader.USER_AGENT);
    }

    @Override
    public String url() {
        return _request.getRequestURI();
    }

    @Override
    public long contentLength() {
        return _request.getContentLength();
    }

    @Override
    public String contentType() {
        return _request.getContentType();
    }

    @Override
    public String body() throws IOException {
        InputStream inpStream = bodyAsStream();

        StringBuilder content = new StringBuilder();
        byte[] b = new byte[1024];
        int lens = -1;
        while ((lens = inpStream.read(b)) > 0) {
            content.append(new String(b, 0, lens));
        }

        return content.toString();
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.getInputStream();
    }

    @Override
    public String param(String key) {
        return _request.getParameter(key);
    }

    @Override
    public String param(String key, String def) {
        String temp = _request.getParameter(key);
        if(XUtil.isEmpty(temp)){
            return def;
        }else{
            return temp;
        }
    }

    @Override
    public int paramAsInt(String key) {
        return Integer.parseInt(param(key,"0"));
    }

    @Override
    public long paramAsLong(String key) {
        return Long.parseLong(param(key,"0"));
    }

    @Override
    public double paramAsDouble(String key) {
        return Double.parseDouble(param(key,"0"));
    }

    @Override
    public XMap paramMap() {
        if(_paramMap == null){
            _paramMap = new XMap();

            Enumeration<String> names = _request.getParameterNames();

            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String value = _request.getParameter(name);
                _paramMap.put(name, value);
            }
        }

        return _paramMap;
    }
    private XMap _paramMap;

    @Override
    public String cookie(String key) {
        return cookie(key,null);
    }

    @Override
    public String cookie(String key, String def) {
        String temp = cookieMap().get(key);
        if(temp == null) {
            return def;
        }else{
            return temp;
        }
    }

    private XMap _cookieMap;

    @Override
    public XMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new XMap();

            Cookie[] _cookies = _request.getCookies();

            if (_cookies != null) {
                for (Cookie c : _cookies) {
                    _cookieMap.put(c.getName(), c.getValue());
                }
            }
        }

        return _cookieMap;
    }

    @Override
    public String header(String key) {
        return _request.getHeader(key);
    }

    @Override
    public String header(String key, String def) {
        String temp = _request.getHeader(key);
        if(XUtil.isEmpty(temp)){
            return def;
        }else{
            return temp;
        }
    }

    @Override
    public XMap headerMap() {
        if(_headerMap == null) {
            _headerMap = new XMap();
            Enumeration<String> headers = _request.getHeaderNames();

            while (headers.hasMoreElements()) {
                String key = (String) headers.nextElement();
                String value = _request.getHeader(key);
                _headerMap.put(key, value);
            }
        }

        return _headerMap;
    }
    private XMap _headerMap;

    @Override
    public String sessionId() {
        return _request.getRequestedSessionId();
    }

    @Override
    public Object session(String key) {
        return _request.getSession().getAttribute(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        _request.getSession().setAttribute(key,val);
    }

    //====================================

    @Override
    public Object response() {
        return _response;
    }

    @Override
    public void charset(String charset) {
        _response.setCharacterEncoding(charset);
    }

    @Override
    public void contentType(String contentType) {
        _response.setContentType(contentType);
    }

    @Override
    public void output(String str) throws IOException {
        PrintWriter writer = _response.getWriter();
        writer.write(str);
        writer.flush();
    }

    @Override
    public void output(InputStream stream) throws IOException {
        OutputStream out = _response.getOutputStream();

        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            out.write(buff, 0, rc);
        }

        out.flush();
    }

    @Override
    public void headerSet(String key, String val) {
        _response.setHeader(key,val);
    }

    @Override
    public void cookieSet(String key, String val, int maxAge) {
        Cookie c = new Cookie(key,val);
        c.setPath("/");
        c.setMaxAge(maxAge);

        _response.addCookie(c);
    }

    @Override
    public void cookieSet(String key, String val, String domain, int maxAge) {
        Cookie c = new Cookie(key,val);
        c.setPath("/");
        c.setMaxAge(maxAge);
        c.setDomain(domain);
        _response.addCookie(c);
    }

    @Override
    public void cookieRemove(String key) {
        Cookie c = new Cookie(key,"");
        c.setPath("/");
        c.setMaxAge(0);

        _response.addCookie(c);
    }

    @Override
    public void redirect(String url) throws IOException {
        redirect(url,302);
    }

    @Override
    public void redirect(String url, int code) throws IOException {
        //_response.sendRedirect(url);
        _response.setHeader(XHeader.LOCATION, url);
        status(code);
    }

    @Override
    public int status() {
        return _response.getStatus();
    }

    @Override
    public void status(int status) throws IOException {
        _response.setStatus(status);
    }
}
