package com.changfeng.officesummoner;

/**
 * Created by changfeng on 2015/5/24.
 */
public class FileInfo {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String name;
    private String path;

    FileInfo() {

    }

    FileInfo(String name, String path) {
        this.name = name;
        this.path = path;
    }
}