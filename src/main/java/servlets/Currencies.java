package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ServletService;

import java.io.*;

@WebServlet(urlPatterns = {"/currencies/*"})
public class Currencies extends HttpServlet {
    ServletService service = new ServletService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            service.getCurrencies(request, response);
        } else if ("POST".equalsIgnoreCase(method)) {
            service.addNewCurrency(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }


}
