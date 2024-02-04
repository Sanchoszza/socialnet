package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import repository.UserRepository;
import utils.Common;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private UserRepository userRepository;

    @Override
    public void init(){
        userRepository = new UserRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<User> users = userRepository.getAllUsers();

            String jsonResponse = convertUsersToJson(users);
            resp.setContentType("application/json");
            resp.getWriter().write(jsonResponse);
        } else {
            try {
                String userIdString = pathInfo.substring(1);
                if (!userIdString.isEmpty()) {
                    Long userId = Long.parseLong(userIdString);
                    User user = userRepository.getUserById(userId);

                    if (user != null) {
                        String jsonResponse = convertObjectToJson(user);
                        resp.setContentType("application/json");
                        resp.getWriter().write(jsonResponse);
                    } else {
                        resp.getWriter().write("User not found");
                    }
                } else {
                    resp.getWriter().write("Invalid user ID format");
                }
            } catch (NumberFormatException e) {
                resp.getWriter().write("Invalid user ID format" + e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        String json = stringBuilder.toString();
        System.out.println("Received JSON: " + json);

        ObjectMapper objectMapper = new ObjectMapper();
        User newUser = objectMapper.readValue(json, User.class);

        newUser.setRegistrationDate(new Date());

        User registeredUser = userRepository.registerUser(newUser);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (registeredUser != null) {

            String jsonResponse = objectMapper.writeValueAsString(registeredUser);
            resp.getWriter().write(jsonResponse);
        } else {

            String errorMessage = "Failed to register user!";
            String jsonErrorResponse = objectMapper.writeValueAsString(errorMessage);
            resp.getWriter().write(jsonErrorResponse);
        }
    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long userId = Long.parseLong(pathInfo.substring(1));
                User existingUser = userRepository.getUserById(userId);

                if (existingUser != null) {
                    String firstName = request.getParameter("firstName");
                    String lastName = request.getParameter("lastName");
                    String email = request.getParameter("email");
                    String passwordHash = request.getParameter("passwordHash");

                    existingUser.setFirstName(firstName);
                    existingUser.setLastName(lastName);
                    existingUser.setEmail(email);
                    existingUser.setPasswordHash(passwordHash);

                    userRepository.updateUser(existingUser);

                    response.getWriter().write("User registered: " + Common.getPrettyGson().toJson(existingUser));
                } else {
                    response.getWriter().write("User not found");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid user ID format");
            }
        } else {
            response.getWriter().write("Invalid request for updating user");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long userId = Long.parseLong(pathInfo.substring(1));
                User existingUser = userRepository.getUserById(userId);

                if (existingUser != null) {
                    userRepository.deleteUser(userId);

                    response.getWriter().write("User registered: " + Common.getPrettyGson().toJson(existingUser));
                } else {
                    response.getWriter().write("User not found");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid user ID format");
            }
        } else {
            response.getWriter().write("Invalid request for deleting user");
        }
    }

    private String convertUsersToJson(List<User> users) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(users);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting list to JSON";
        }
    }

    private String convertObjectToJson(User user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(user);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error converting object to JSON";
        }
    }
}
