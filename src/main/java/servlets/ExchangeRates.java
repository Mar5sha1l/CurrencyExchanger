package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ServletService;

import java.io.IOException;

@WebServlet(urlPatterns = {"/exchangeRates/*"})
public class ExchangeRates extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletService service = new ServletService();

        String method = req.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            service.getExchangeRates(req, resp);
        } else if ("POST".equalsIgnoreCase(method)) {
            service.addExchangeRate(req, resp);
        } else if ("PATCH".equalsIgnoreCase(method)) {
            service.updateExchangeRate(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
}
