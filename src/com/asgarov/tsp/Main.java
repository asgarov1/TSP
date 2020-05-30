package com.asgarov.tsp;

import com.asgarov.tsp.map.TspMap;

public class Main {
    public static void main(String[] args) {
        TspMap map = new TspMap(args[1]);

        map.displayMatrix();
        System.out.println();

        if (args[0].equals("-e")) {
            map.shortestRouteViaEnumeration();
        } else if (args[0].equals("-n")) {
            map.shortestRouteViaNearestNeighbour();
        }

    }
}
