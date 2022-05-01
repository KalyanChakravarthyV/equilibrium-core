package in.vadlakonda.equilibrium.dispatch.config;

import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;

public class Dispatcher {

    private String resourceURI;

    private String dispatcher;
    private String configFile;

    private String resourceRoot;

    private RequestDispatcher requestDispatcher;
    private DispatcherConfig dispatcherConfig;


    public String getResourceURI() {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    public RequestDispatcher getRequestDispatcher() {
        return requestDispatcher;
    }

    public void setRequestDispatcher(RequestDispatcher RequestDispatcher) {
        this.requestDispatcher = RequestDispatcher;
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
    }

    public DispatcherConfig getDispatcherConfig() {
        return dispatcherConfig;
    }

    public void setDispatcherConfig(DispatcherConfig dispatcherConfig) {
        this.dispatcherConfig = dispatcherConfig;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getResourceRoot() {
        return resourceRoot;
    }

    public void setResourceRoot(String resourceRoot) {
        this.resourceRoot = resourceRoot;
    }
}
