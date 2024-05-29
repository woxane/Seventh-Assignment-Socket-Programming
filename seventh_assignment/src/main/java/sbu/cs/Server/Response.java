package sbu.cs.Server;

import java.util.ArrayList;

public class Response {
    private ArrayList<String> fileNames;

    public Response() {
        //Default constructor for Jackson
    }

    public Response(ArrayList<String> fileNames) {
        this.fileNames = fileNames;
    }

    public ArrayList<String> getFileNames() {
        return this.fileNames;
    }
}
