package in.vadlakonda.equilibrium.mock.servlet;

import javax.servlet.http.HttpServletResponse;

public abstract class MockHttpServletResponse implements HttpServletResponse {

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    protected String contentType;
}
