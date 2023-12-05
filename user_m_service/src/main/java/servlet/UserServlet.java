package servlet;

import model.User;
import repository.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        if (pathInfo == null || pathInfo.equals("/")){
            List<User> users = userRepository.getAllUsers();
            resp.getWriter().write(users.toString());
        } else {
            try{
                Long userId = Long.parseLong(pathInfo.substring(1));
                User user = userRepository.getUserById(userId);

                if (user != null){
                    resp.getWriter().write(user.toString());
                } else {
                    resp.getWriter().write("User not found!");
                }
            } catch (NumberFormatException e){
                resp.getWriter().write("Invalid user ID format");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName =  req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String passwordHash = req.getParameter("passwordHash");

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordHash);
        newUser.setRegistrationDate(new Date());

        User registeredUser = userRepository.registerUser(newUser);

        if (registeredUser != null){
            resp.getWriter().write("User registered: " + registeredUser.toString());
        } else {
            resp.getWriter().write("Failed to register user!");
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

                    response.getWriter().write("User updated: " + existingUser.toString());
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

                    response.getWriter().write("User deleted: " + existingUser.toString());
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
}
