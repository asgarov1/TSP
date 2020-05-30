package com.asgarov.tsp;

import com.asgarov.tsp.map.TspMap;

public class Main {
    public static void main(String[] args) {
        TspMap map = new TspMap("file.txt");

        map.displayMatrix();
        System.out.println();

        map.shortestRouteViaEnumeration();

        System.out.println();
        map.shortestRouteViaNearestNeighbour();

    }
}
