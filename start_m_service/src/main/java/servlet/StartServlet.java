package servlet;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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

        if (pathInfo.startsWith("/users")) {
            if (pathInfo.length() > "/users".length()) {
                String id = pathInfo.substring("/users/".length());
                handleGetRequest("http://localhost:8026/users/" + id, resp);
            } else {
                handleGetRequest("http://localhost:8026/users", resp);
            }
        } else if (pathInfo.startsWith("/posts")) {
            if (pathInfo.length() > "/posts".length()) {
                String id = pathInfo.substring("/posts/".length());
                handleGetRequest("http://localhost:8027/posts/" + id, resp);
            } else {
                handleGetRequest("http://localhost:8027/posts", resp);
            }
        } else if (pathInfo.startsWith("/likes")) {
            if (pathInfo.length() > "/likes".length()) {
                String id = pathInfo.substring("/likes/".length());
                handleGetRequest("http://localhost:8024/likes/" + id, resp);
            } else {
                handleGetRequest("http://localhost:8024/likes", resp);
            }
        } else if (pathInfo.startsWith("/comments")) {
            if (pathInfo.length() > "/comments".length()) {
                String id = pathInfo.substring("/comments/".length());
                handleGetRequest("http://localhost:8023/comments/" + id, resp);
            } else {
                handleGetRequest("http://localhost:8023/comments", resp);
            }
        }
    }

    private void handleGetRequest(String url, HttpServletResponse resp) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();
        String responseString = IOUtils.toString(entity.getContent());

        resp.setContentType("application/json");
        resp.getWriter().write(responseString);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo.startsWith("/users")) {
            if (pathInfo.length() > "/users".length()) {
                String id = pathInfo.substring("/users/".length());
                handlePostRequest("http://localhost:8026/users/" + id, req, resp);
            } else {
                handlePostRequest("http://localhost:8026/users", req, resp);
            }
        } else if (pathInfo.startsWith("/posts")) {
            if (pathInfo.length() > "/posts".length()) {
                String id = pathInfo.substring("/posts/".length());
                handlePostRequest("http://localhost:8027/posts/" + id, req, resp);
            } else {
                handlePostRequest("http://localhost:8027/posts", req, resp);
            }
        } else if (pathInfo.startsWith("/likes")) {
            if (pathInfo.length() > "/likes".length()) {
                String id = pathInfo.substring("/likes/".length());
                handlePostRequest("http://localhost:8024/likes/" + id, req, resp);
            } else {
                handlePostRequest("http://localhost:8024/likes", req, resp);
            }
        } else if (pathInfo.startsWith("/comments")) {
            if (pathInfo.length() > "/comments".length()) {
                String id = pathInfo.substring("/comments/".length());
                handlePostRequest("http://localhost:8023/comments/" + id, req, resp);
            } else {
                handlePostRequest("http://localhost:8023/comments", req, resp);
            }
        }
    }

    private void handlePostRequest(String url, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);

        String requestData = IOUtils.toString(req.getInputStream(), "UTF-8");
        request.setEntity(new StringEntity(requestData));

        HttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();
        String responseString = IOUtils.toString(entity.getContent());

        resp.setContentType("application/json");
        resp.getWriter().write(responseString);
    }


    //    @Override
//    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String pathInfo = req.getPathInfo();
//        if (pathInfo == null || pathInfo.equals("/")) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//        String[] pathParts = pathInfo.split("/");
//        if (pathParts.length < 3) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//        String id = pathParts[2];
//
//        if (pathInfo.startsWith("/users/")){
//            handleDeleteRequest("http://localhost:8026/users/" + id, resp);
//        } else if (pathInfo.startsWith("/posts/")){
//            handleDeleteRequest("http://localhost:8027/posts/" + id, resp);
//        } else if (pathInfo.startsWith("/likes/")){
//            handleDeleteRequest("http://localhost:8024/likes/" + id, resp);
//        } else if (pathInfo.startsWith("/comments/")){
//            handleDeleteRequest("http://localhost:8023/comments/" + id, resp);
//        }
//    }
//
//    private void handleDeleteRequest(String url, HttpServletResponse resp) throws IOException {
//        HttpClient client = HttpClientBuilder.create().build();
//        HttpDelete deleteRequest = new HttpDelete(url);
//
//        HttpResponse response = client.execute(deleteRequest);
//
//        HttpEntity entity = response.getEntity();
//        String responseString = IOUtils.toString(entity.getContent());
//
//        resp.setContentType("application/json");
//        resp.getWriter().write(responseString);
//    }
}


//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String pathInfo = req.getPathInfo();
//        if (pathInfo == null || pathInfo.equals("/")) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//        String[] pathParts = pathInfo.split("/");
//        if (pathParts.length < 3) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//        String id = pathParts[2];
//
//        if (pathInfo.startsWith("/users/")){
//            handlePostRequest("http://localhost:8026/users/" + id, req, resp);
//        } else if (pathInfo.startsWith("/posts/")){
//            handlePostRequest("http://localhost:8027/posts/" + id, req, resp);
//        } else if (pathInfo.startsWith("/likes/")){
//            handlePostRequest("http://localhost:8024/likes/" + id, req, resp);
//        } else if (pathInfo.startsWith("/comments/")){
//            handlePostRequest("http://localhost:8023/comments/" + id, req, resp);
//        }
//    }
//
//

//
//    private void handlePostRequest(String url, HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        HttpClient client = HttpClientBuilder.create().build();
//        HttpPost postRequest = new HttpPost(url);
//
//        String requestData = IOUtils.toString(req.getReader());
//
//        postRequest.setEntity(new StringEntity(requestData));
//
//        HttpResponse response = client.execute(postRequest);
//
//        HttpEntity entity = response.getEntity();
//        String responseString = IOUtils.toString(entity.getContent());
//
//        resp.setContentType("application/json");
//        resp.getWriter().write(responseString);
//    }

