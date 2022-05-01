package in.vadlakonda.equilibrium.dispatch.config;

import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;

import java.util.ArrayList;
import java.util.List;

public class DispatcherConfig {

    private String baseURI;

    private List<Dispatcher> dispatchers = new ArrayList<>(0);

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public List<Dispatcher> getDispatchers() {
        return dispatchers;
    }

    public void setDispatchers(List<Dispatcher> dispatchers) {
        this.dispatchers = dispatchers;
    }
}
