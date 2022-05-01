package in.vadlakonda.equilibrium.api.request;

public class Payload {

    private  String action;

    //Support for Multi-part form data can make this redundant
    private String body;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
