package de.simplicit.vjdbc.test.junit.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import de.simplicit.vjdbc.servlet.ServletCommandSinkIdentifier;

public class CheckPropertyFilter implements Filter {
    public void destroy() {
        System.out.println("CheckProperty-Filter destroyed !");
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hreq = (HttpServletRequest)req;
        String method = hreq.getHeader(ServletCommandSinkIdentifier.METHOD_IDENTIFIER);
                
        if(method.equals(ServletCommandSinkIdentifier.CONNECT_COMMAND)) {
            String testValue = hreq.getHeader("connect-test-property");
            if(!testValue.equals("connect-test-value")) {
                throw new ServletException("Test-Property hasn't the expected value");
            }
        }
        else if(method.equals(ServletCommandSinkIdentifier.PROCESS_COMMAND)) {
            String testValue = hreq.getHeader("process-test-property");
            if(!testValue.equals("process-test-value")) {
                throw new ServletException("Test-Property hasn't the expected value");
            }
        }
        
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {
        System.out.println("CheckProperty-Filter initialized !");
    }
}
