package com.asgarov.tsp;

import com.asgarov.util.FileReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.asgarov.util.ArrayUtil.copyArray;

public class TspMap {

    private static int count;
    private double shortestDistance;
    private City[] shortestRoute;
    private City[] cities;
    private final Map<City, Map<City, Double>> distances = new HashMap<>();

    public double distanceBetween(City a, City b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    public TspMap(String fileName) {
        loadMap(fileName);
    }

    private void loadMap(String fileName) {
        List<String> lines = null;
        try {
            lines = new FileReader().readFile(fileName);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        cities = lines.stream().skip(1).map(line -> {
            double x = Double.parseDouble(line.split(" ")[0]);
            double y = Double.parseDouble(line.split(" ")[1]);
            return new City(x, y);
        }).toArray(City[]::new);

        char ASCII_A = 65;
        for (City city : cities) {
            city.setName("" + (char) ASCII_A++);
        }

        saveDistances(cities);
    }

    private void saveDistances(City[] cities) {
        for (City city : cities) {
            Map<City, Double> temp = new HashMap<>();
            for (City c : cities) {
                temp.put(c, distanceBetween(city, c));
            }
            distances.put(city, temp);
        }
    }

    public void displayMatrix() {
        //Print first line
        System.out.print("   |");
        for (City city : cities) {
            System.out.print("  " + city.getName() + "  |");
        }
        System.out.println();

        //Rest of matrix
        for (City city : cities) {
            System.out.print(" " + city.getName() + " |" + " ");
            for (City c : cities) {
                System.out.printf("%.1f | ", distances.get(city).get(c));
            }
            System.out.println();
        }
    }

    public void displayAllRoutes() {
        permute(cities, cities.length);
    }

    private void swap(City[] cities, int indexA, int indexB) {
        City temp = cities[indexA];
        cities[indexA] = cities[indexB];
        cities[indexB] = temp;
    }

    public void permute(City[] cities, int n) {
        if (n == 1) {
            City[] route = turnPermutationIntoCircularRoute(cities);
            saveIfBestRoute(route);
            printRoute(route);
        } else {
            for (int i = 0; i < n; i++) {
                permute(cities, n - 1);
                if (n % 2 == 1) {
                    swap(cities, 0, n - 1);
                } else {
                    swap(cities, i, n - 1);
                }
            }
        }
    }

    private City[] turnPermutationIntoCircularRoute(City[] permutation) {
        City[] temp = new City[permutation.length + 1];
        for (int i = 0; i < permutation.length; i++) {
            temp[i] = permutation[i];
        }
        temp[permutation.length] = permutation[0];
        return temp;
    }

    private void saveIfBestRoute(City[] route) {
        double distance = calculateDistance(route);

        if (shortestDistance == 0 || distance < this.shortestDistance) {
            shortestDistance = distance;
            shortestRoute = copyArray(route);
        }
    }

    private double calculateDistance(City[] route) {
        double distance = 0;
        for (int i = 0; i < route.length - 1; i++) {
            distance += distances.get(route[i]).get(route[i + 1]);
        }
        return distance;
    }

    public void printRoute(City[] cities) {
        System.out.printf("%3d. ", ++count);
        for (City city : cities) {
            System.out.print(city.getName() + " ");
        }
        System.out.println("| distance: " + calculateDistance(cities));
    }


//  Getters
    public double getShortestDistance() {
        return shortestDistance;
    }

    public City[] getShortestRoute() {
        return shortestRoute;
    }
}
