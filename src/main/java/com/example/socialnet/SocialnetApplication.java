package com.example.socialnet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.PostServlet;
import servlet.UserServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SocialnetApplication {

	private static Logger log = LoggerFactory.getLogger(SocialnetApplication.class.getSimpleName());

	private static final int USER_SERVICE_PORT = 8026;
	private static final int POST_SERVICE_PORT = 8027;

	public static void main(String[] args) throws Exception {

		startMicroservice(UserServlet.class, USER_SERVICE_PORT);
		startMicroservice(PostServlet.class, POST_SERVICE_PORT);

		Thread.sleep(2000);

		String userApiResponse = sendHttpRequest("http://localhost:" + USER_SERVICE_PORT + "/users/getUser/123");
		String postApiResponse = sendHttpRequest("http://localhost:" + POST_SERVICE_PORT + "/posts/getPost/456");

		log.info("User Service Response: {}", userApiResponse);
		log.info("Post Service Response: {}", postApiResponse);

		stopMicroservice(USER_SERVICE_PORT);
		stopMicroservice(POST_SERVICE_PORT);
	}

	private static void startMicroservice(Class<? extends javax.servlet.http.HttpServlet> servletClass, int port) throws Exception {
		Server server = new Server(port);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		context.addServlet(new ServletHolder(servletClass), "/*");

		server.setHandler(context);

		try {
			server.start();
			log.info("{} Service started on port {}", servletClass.getSimpleName(), port);
			server.join();
		} catch (Exception e) {
			log.error("Error while starting {} Service", servletClass.getSimpleName(), e);
		}
	}

	private static void stopMicroservice(int port) throws Exception {
		Server server = new Server(port);

		try {
			server.stop();
			log.info("Service on port {} stopped", port);
		} catch (Exception e) {
			log.error("Error while stopping service on port {}", port, e);
		}
	}


	private static String sendHttpRequest(String url) throws Exception {
		StringBuilder response = new StringBuilder();

		try {
			URL apiUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
				}
			} else {
				log.error("HTTP request failed with response code: {}", responseCode);
			}
		} catch (Exception e) {
			log.error("Error during HTTP request", e);
		}

		return response.toString();
	}
}
