package com.ppdai.framework.raptor.service;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.exception.RaptorFrameworkException;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyServletEndpoint extends ServletEndpoint {

    @Getter
    private Server server;

    public JettyServletEndpoint(URL baseUrl) {
        super(baseUrl);
        initJettyServer();
    }

    private void initJettyServer() {
        server = new Server(baseUrl.getPort());
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(DefaultServlet.class, "/");
        ServletHolder servletHolder = new ServletHolder(this);
        String pathSpec = StringUtils.removeEnd(baseUrl.getPath(), RaptorConstants.PATH_SEPARATOR) + RaptorConstants.PATH_SEPARATOR + "*";
        servletContextHandler.addServlet(servletHolder, pathSpec);
        try {
            server.start();
        } catch (Exception e) {
            throw new RaptorFrameworkException("Start jetty server error.", e);
        }
    }
}
