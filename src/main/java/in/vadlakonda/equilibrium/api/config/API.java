package in.vadlakonda.equilibrium.api.config;

import in.vadlakonda.equilibrium.api.EquilibriumAPI;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;

public class API {

    private String apiOperation;
    private String apiClass;
    private EquilibriumAPI api;

    public String getApiOperation() {
        return apiOperation;
    }

    public void setApiOperation(String apiOperation) {
        this.apiOperation = apiOperation;
    }

    public EquilibriumAPI getApi() {
        return api;
    }

    public void setApi(EquilibriumAPI api) {
        this.api = api;
    }

    public String getApiClass() {
        return apiClass;
    }

    public void setApiClass(String apiClass) {
        this.apiClass = apiClass;
    }
}
