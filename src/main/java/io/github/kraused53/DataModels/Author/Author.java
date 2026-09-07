package io.github.kraused53.DataModels.Author;

public class Author {
    private int id;
    private String name;

    public Author() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int tab) {
        StringBuilder sb = new StringBuilder();

        String indent = "\t".repeat(tab);

        sb.append(indent).append(name);

        return sb.toString();
    }
}
