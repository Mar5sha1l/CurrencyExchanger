package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.*;

@WebServlet(urlPatterns = {"/currencies", "/currency/*"})
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService service = new CurrencyService();

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
