package in.vadlakonda.equilibrium.api.response;

import java.util.ArrayList;
import java.util.List;

public class ScriptEngineResponse {

    String out ;
    String err;

    public ScriptEngineResponse(String out, String err) {
        this.out = out;
        this.err = err;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}


