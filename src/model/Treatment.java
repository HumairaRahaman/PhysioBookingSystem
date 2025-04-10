package model;

public class Treatment {
    private String name;
    private String description;

    public Treatment(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Treatment treatment = (Treatment) obj;
        return name.equals(treatment.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
