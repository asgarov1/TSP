package com.asgarov.tsp.map;

import com.asgarov.tsp.util.CityArrayUtil;
import com.asgarov.tsp.util.FileReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.asgarov.tsp.util.CityArrayUtil.copyArray;

public class TspMap {
    /**
     * count is used to display the number of permutation if these are displayed
     */
    private static int count;

    /**
     * variable used to store shortest distance
     */
    private double shortestDistance;

    /**
     * variable used to store the shortest route
     */
    private City[] shortestRoute;

    /**
     * Array of initial cities, also changed during the permutations generation for the complete enumeration
     */
    private City[] cities;

    /**
     * A Hashmap saving for each city another hashmap of other cities and distances to each
     */
    private final Map<City, Map<City, Double>> distances = new HashMap<>();

    /**
     * Constructor
     * @param fileName requires the file to load the map from
     */
    public TspMap(String fileName) {
        loadMap(fileName);
    }

    /**
     * loads the map from the file
     * creates the array of cities with the coordinates
     * each city is given a capital letter name starting from A for easier representation
     * @param fileName
     */
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

    /**
     * For better performance distances between cities are saved so that they don't have to be repeatedly calculated
     * @param cities
     */
    private void saveDistances(City[] cities) {
        for (City city : cities) {
            Map<City, Double> temp = new HashMap<>();
            for (City c : cities) {
                temp.put(c, distanceBetween(city, c));
            }
            distances.put(city, temp);
        }
    }

    /**
     * displays the adjacency matrix for the graph
     */
    public void displayMatrix() {
        //Print the first line
        System.out.print("   |");
        for (City city : cities) {
            System.out.print("  " + city.getName() + "  |");
        }
        System.out.println();

        //Rest of the matrix
        for (City city : cities) {
            System.out.print(" " + city.getName() + " |" + " ");
            for (City c : cities) {
                System.out.printf("%.1f | ", distances.get(city).get(c));
            }
            System.out.println();
        }
    }

    /**
     * switches the cities in an array
     * helper method for the heap's algorithm
     * @param cities
     * @param indexA
     * @param indexB
     */
    private void swap(City[] cities, int indexA, int indexB) {
        City temp = cities[indexA];
        cities[indexA] = cities[indexB];
        cities[indexB] = temp;
    }

    /**
     * generates permutations using Heap's algorithm
     * when a permutation is generated it is turned into a route
     * @param cities
     * @param n - required for recursive call
     */
    public void permute(City[] cities, int n) {
        if (n == 1) {
            City[] route = turnPermutationIntoCircularRoute(cities);
            saveIfBestRoute(route);
//            uncomment to show the list of routes to be printed
//            printRoute(route);
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

    /**
     * Adds the first city to the end in order to turn the permutation into a circle
     * @param permutation
     * @return
     */
    private City[] turnPermutationIntoCircularRoute(City[] permutation) {
        City[] temp = new City[permutation.length + 1];
        for (int i = 0; i < permutation.length; i++) {
            temp[i] = permutation[i];
        }
        temp[permutation.length] = permutation[0];
        return temp;
    }

    /**
     * Checks whether the route has the shortest distance so far and if so saves it
     * @param route
     */
    private void saveIfBestRoute(City[] route) {
        double distance = calculateDistance(route);

        if (shortestDistance == 0 || distance < this.shortestDistance) {
            shortestDistance = distance;
            shortestRoute = copyArray(route);
        }
    }

    /**
     * Calculates complete distance between the cities on route
     * @param route
     * @return
     */
    private double calculateDistance(City[] route) {
        double distance = 0;
        for (int i = 0; i < route.length - 1; i++) {
            distance += distances.get(route[i]).get(route[i + 1]);
        }
        return distance;
    }

    /**
     * calculates the distance between 2 cities using the Pythagorean algorithm
     * @param a
     * @param b
     * @return
     */
    public double distanceBetween(City a, City b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * prints the route with distance shown
     * @param cities
     */
    public void printRoute(City[] cities) {
        System.out.printf("%3d. ", ++count);
        for (City city : cities) {
            System.out.print(city.getName() + " ");
        }
        System.out.println("| distance: " + calculateDistance(cities));
    }

    /**
     * This method is used independent whether nearest neighbour heuristic or enumeration was chosen
     * in either case the correct function is passed as a parameter into this method.
     * This function measures the time that the calculate took and display the result, distance and time
     * @param runnable - a functional interface that takes no parameters and returns nothing allowing me
     *                 to pass functions as parameters
     */
    public void shortestRoute(Runnable runnable) {
        long start = System.nanoTime();
        runnable.run();
        long finish = System.nanoTime();
        long timeElapsed = finish - start;

        System.out.print("Shortest route: ");
        CityArrayUtil.printArray(getShortestRoute());
        System.out.print("| distance: " + getShortestDistance());
        System.out.printf("| time: %,d ns", timeElapsed);
    }

    /**
     * Method that is called for the nearest neighbour algorithm
     */
    public void shortestRouteViaNearestNeighbour() {
        shortestRoute(this::nearestNeighbour);
    }

    /**
     * Method called for the complete enumeration
     */
    public void shortestRouteViaEnumeration() {
        shortestRoute(() -> permute(cities, cities.length));
    }

    /**
     * Actual function that calculated all the logic of nearest neighbour algorithm
     * Starts by creating a stack of cities left
     * From them it one by one calculates the nearest and chooses it for the route and removes it from the stack of cities left
     * After calculation is finished the array of the cities is saved as shortest route and shortest distance
     */
    public void nearestNeighbour() {
        List<City> citiesStack = citiesLeft();

        City[] finalRoute = new City[cities.length];
        finalRoute[0] = cities[0];
        for (int i = 1; i < finalRoute.length; i++) {
            int index = 0;
            double shortestDistance = distances.get(finalRoute[i - 1]).get(citiesStack.get(0));
            for (int j = 0; j < citiesStack.size(); j++) {
                double distance = distances.get(finalRoute[i - 1]).get(citiesStack.get(j));
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

    /**
     * return the list of cities left for the route
     * the first one (with index 0) is left out as it is a starting point already picked for final route
     * @return
     */
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
