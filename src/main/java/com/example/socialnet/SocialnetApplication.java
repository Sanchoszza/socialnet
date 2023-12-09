package com.example.socialnet;

import jakarta.servlet.Servlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.CommentServlet;
import servlet.LikeServlet;
import servlet.PostServlet;
import servlet.UserServlet;

public class SocialnetApplication {

	private static Logger log = LoggerFactory.getLogger(SocialnetApplication.class.getSimpleName());

	private static Server server;

	public static void main(String[] args) throws Exception {
		runServer(8080, "/");
	}

	public static void runServer(int port, String contextStr) {
		server = new Server(port);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextStr);

		context.addServlet(new ServletHolder((Servlet) new CommentServlet()), "/comment/*");
		context.addServlet(new ServletHolder((Servlet) new LikeServlet()), "/likes/*");
		context.addServlet(new ServletHolder((Servlet) new PostServlet()), "/posts/*");
		context.addServlet(new ServletHolder((Servlet) new UserServlet()), "/users/*");

		context.addServlet(DefaultServlet.class, "/");

		server.setHandler(context);

		try {
			server.start();
			log.info("Server started on port {}", port);
			server.join();
		} catch (Exception e) {
			log.error("Error while starting the server", e);
		}
	}

	public static void stopServer() {
		try {
			if (server != null && server.isRunning()) {
				server.stop();
				log.info("Server stopped");
			}
		} catch (Exception e) {
			log.error("Error while stopping the server", e);
		}
	}
}
