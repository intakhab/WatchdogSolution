package com.hcl.dog.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hcl.dog.service.BaseServicer;
/***
 * 
 * @author intakhabalam.s@hcl.com	
 * All time Filter will intercept the 
 * URL hit and check user session is valid or not
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebFilter implements Filter {
	@Autowired
	private BaseServicer baseServicer;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
			HttpServletRequest req = (HttpServletRequest) request;
			String reqURL=req.getServletPath().replaceFirst("/","");
            // Goes to default page
			if (!"run".equals(reqURL) && !"dologin".equals(reqURL) 
					&& baseServicer.getCurrentUserInfo(req.getSession()) == null) {
				req.getRequestDispatcher("login")
                       .forward(request,response);
		    }else {
		    	chain.doFilter(request, response); 
		    }
	}
	
	@Override
	public void destroy() {
	}
}
