package main.model;

import java.util.Objects;

public class Node {
    private int id;
    private String name;

    // 坐标，用于A*
    private double x;
    private double y;

    public Node(int id, String name, double x, double y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Node other)) return false;

        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
