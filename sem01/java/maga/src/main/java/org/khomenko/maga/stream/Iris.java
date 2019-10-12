package org.khomenko.maga.stream;

import org.khomenko.maga.csv.CsvColumn;

enum Petal {
    SMALL, MEDIUM, LARGE,
}

public class Iris {
    @CsvColumn
    private Double sepalLength; // длина чашелистника

    @CsvColumn
    private double sepalWidth; // ширина чашелистника

    @CsvColumn
    private double petalLength; // длина лепестка

    @CsvColumn
    private double petalWidth; // ширина лепестка

    @CsvColumn
    private String species; // вид

    public Iris() {}

    public Iris(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String species) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.species = species;
    }

    public double getSepalLength() {
        return sepalLength;
    }

    public double getSepalWidth() {
        return sepalWidth;
    }

    public double getPetalLength() {
        return petalLength;
    }

    public double getPetalWidth() {
        return petalWidth;
    }

    public String getSpecies() {
        return species;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this.getClass() != other.getClass()) {
            return false;
        }

        var iris = (Iris)other;

        return getPetalLength() == iris.getPetalLength() &&
                getPetalWidth() == iris.getPetalWidth() &&
                getSepalLength() == iris.getSepalLength() &&
                getSepalWidth() == iris.getSepalWidth() &&
                getSpecies().equals(iris.getSpecies());
    }

    @Override
    public String toString() {
        return "Iris{" +
                "sepalLength=" + sepalLength +
                ", sepalWidth=" + sepalWidth +
                ", petalLength=" + petalLength +
                ", petalWidth=" + petalWidth +
                ", species='" + species + "'" +
                '}';
    }
}

