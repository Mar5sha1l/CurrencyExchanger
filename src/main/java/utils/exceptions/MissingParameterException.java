package utils.exceptions;

public class MissingParameterException extends RuntimeException {
    public MissingParameterException(String parameter) {
        super("Missing required parameter: " + parameter);
    }
}
