package servlet;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo.equals("/users")){
            String urlUser = "http://localhost:8026/users";
            HttpClient clientUser = HttpClientBuilder.create().build();
            HttpGet requestUser = new HttpGet(urlUser);

            HttpResponse responseUser = clientUser.execute(requestUser);

            HttpEntity respUser = responseUser.getEntity();
            String respStrUser = IOUtils.toString(respUser.getContent());

            resp.getWriter().write(respStrUser);
        } else if (pathInfo.equals("/posts")){
            String urlPost = "http://localhost:8027/posts";
            HttpClient clientPost = HttpClientBuilder.create().build();
            HttpGet requestPost = new HttpGet(urlPost);

            HttpResponse responsePost = clientPost.execute(requestPost);

            HttpEntity respPost = responsePost.getEntity();
            String respStrPost = IOUtils.toString(respPost.getContent());

            resp.getWriter().write(respStrPost);
        } else if (pathInfo.equals("/likes")){
            String urlLikes = "http://localhost:8024/likes";
            HttpClient clientLikes = HttpClientBuilder.create().build();
            HttpGet requestLikes = new HttpGet(urlLikes);

            HttpResponse responseLikes = clientLikes.execute(requestLikes);

            HttpEntity respLikes = responseLikes.getEntity();
            String respStrLikes = IOUtils.toString(respLikes.getContent());

            resp.getWriter().write(respStrLikes);
        } else if (pathInfo.equals("/comments")){
            String urlComments = "http://localhost:8023/comments";
            HttpClient clientComments = HttpClientBuilder.create().build();
            HttpGet requestCommetns = new HttpGet(urlComments);

            HttpResponse responseComments = clientComments.execute(requestCommetns);

            HttpEntity respComments = responseComments.getEntity();
            String respStrComments = IOUtils.toString(respComments.getContent());

            resp.getWriter().write(respStrComments);
        }

    }


}
