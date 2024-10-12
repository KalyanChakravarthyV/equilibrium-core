package in.vadlakonda.equilibrium.api.config;

import java.util.ArrayList;
import java.util.List;

public class APIConfig {

    private String baseURI;

    private List<API> apis = new ArrayList<>(0);

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public List<API> getApis() {
        return apis;
    }

    public void setApis(List<API> apis) {
        this.apis = apis;
    }
}
