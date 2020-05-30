package com.asgarov.tsp.map;

import com.asgarov.tsp.util.ArrayUtil;
import com.asgarov.tsp.util.FileReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.BiConsumer;

import static com.asgarov.tsp.util.ArrayUtil.copyArray;

public class TspMap {

    private static int count;
    private double shortestDistance;
    private int calculationTime;
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

    public void shortestRoute(Runnable runnable) {
        long start = System.nanoTime();
        runnable.run();
        long finish = System.nanoTime();
        long timeElapsed = finish - start;

        System.out.print("Shortest route: ");
        ArrayUtil.printArray(getShortestRoute());
        System.out.print("| distance: " + getShortestDistance());
        System.out.printf("| time: %,d ns", timeElapsed);
    }

    public void shortestRouteViaNearestNeighbour() {
        shortestRoute(this::nearestNeighbour);
    }

    public void shortestRouteViaEnumeration() {
        shortestRoute(() -> permute(cities, cities.length));
    }

    public void nearestNeighbour() {
        List<City> citiesStack = citiesLeft();

        City[] finalRoute = new City[cities.length];
        finalRoute[0] = cities[0];
        for (int i = 1; i < finalRoute.length; i++) {
            int index = 0;
            double shortestDistance = distanceBetween(finalRoute[i - 1], citiesStack.get(0));
            for (int j = 0; j < citiesStack.size(); j++) {
                double distance = distanceBetween(finalRoute[i - 1], citiesStack.get(j));
                if (distance < shortestDistance) {
                    index = j;
                    shortestDistance = distance;
                }
            }
            City city = citiesStack.get(index);
            citiesStack.remove(city);
            finalRoute[i] = city;
        }
        shortestRoute = turnPermutationIntoCircularRoute(finalRoute);
        shortestDistance = calculateDistance(shortestRoute);
    }

    private List<City> citiesLeft() {
        List<City> citiesStack = new ArrayList<>();
        for (int i = 1; i < cities.length; i++) {
            citiesStack.add(cities[i]);
        }
        return citiesStack;
    }

    //  Getters
    public double getShortestDistance() {
        return shortestDistance;
    }

    public City[] getShortestRoute() {
        return shortestRoute;
    }
}
