package sbu.cs.Server;

public class Request {
    private int index;

    public Request() {
        //Default constructor for Jackson
    }

    public Request(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
