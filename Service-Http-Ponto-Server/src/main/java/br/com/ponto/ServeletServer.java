package br.com.ponto;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServeletServer {
    public static void main(String[] args) throws Exception {
        var server = new Server(8080);

        var context = new ServletContextHandler();
        context.setContextPath("/register-point");
        context.addServlet(new ServletHolder(new newPointRequest()),"/new");
        server.setHandler(context);

        server.start();
        server.join();
    }
}
