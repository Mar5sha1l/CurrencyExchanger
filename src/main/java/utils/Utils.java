package utils;

import jakarta.servlet.http.HttpServletRequest;
import model.ExchangeRate;
import utils.exceptions.MissingParameterException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

    public static Map<String, String> getUrlParametersAsMap(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()[0]
                ));
    }

    public static String convertExchangeRatesToJson(List<ExchangeRate> exchangeRates) {
        StringBuilder result = new StringBuilder("[");
        boolean first = true;

        for (ExchangeRate exchangeRate : exchangeRates) {
            if (!first) {
                result.append(", ");
            }
            first = false;
            result.append(exchangeRate.toJson());
        }

        result.append("]");
        return result.toString();
    }

    public static String validateParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null) {
            throw new MissingParameterException(paramName);
        }
        return value;
    }
}
