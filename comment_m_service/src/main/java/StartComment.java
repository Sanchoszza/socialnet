import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.CommentServlet;

public class StartComment {

    private static Logger log = LoggerFactory.getLogger(StartComment.class.getSimpleName());

    private static final int PORT = 8023;
    private static final String CONTEXT_PATH = "/";

    private static Server server;

    public static void main(String[] args) throws Exception {
        runServer(PORT, CONTEXT_PATH);
    }

    public static void runServer(int port, String contextPath){
        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(contextPath);

        context.addServlet(new ServletHolder(new CommentServlet()), "/comments/*");

        server.setHandler(context);

        try {
            server.start();
            log.info("Server started on port {}", port);
            server.join();
        } catch (Exception e) {
            log.error("Error while starting the server", e);
        }
    }

    public static void stopServer() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
            log.info("Server stopped");
        }
    }
}
