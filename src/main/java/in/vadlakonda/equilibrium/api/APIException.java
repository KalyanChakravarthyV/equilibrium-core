package in.vadlakonda.equilibrium.api;

/*
 * For handling HTTP 50X, 40X and maybe 302 ?
 */
public class APIException extends Exception {

    private int statusCode;
    private String message;

    public APIException(int statusCode, String message) {
        this.setStatusCode(statusCode);
        this.setMessage(message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
