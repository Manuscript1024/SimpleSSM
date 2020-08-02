package model;

public class Role {

    public String id;

    public String name;

    public Root root;

    public String toString(){
        return this.id + ":" + this.name + ":" + this.root.id;
    }
}
