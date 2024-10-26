package in.vadlakonda.equilibrium.api.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Directory {

    String name;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    String path;
    int id;
    Date lastModified;

    List<File> files = new ArrayList<>();

    public Directory(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
