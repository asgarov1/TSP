package com.asgarov.tsp;

import com.asgarov.util.ArrayUtil;

public class Main {
    public static void main(String[] args) {
        TspMap map = new TspMap("file.txt");

        map.displayMatrix();
        System.out.println();

        map.displayAllRoutes();
        System.out.println();

        System.out.print("Shortest route: ");
        ArrayUtil.printArray(map.getShortestRoute());
        System.out.println("| distance: " + map.getShortestDistance());
    }
}
