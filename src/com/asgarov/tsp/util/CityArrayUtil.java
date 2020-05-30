package com.asgarov.tsp.util;

import com.asgarov.tsp.map.City;

public class CityArrayUtil {
    /**
     * Helper method to copy an array of cities
     * @param cities
     * @return
     */
    public static City[] copyArray(City[] cities) {
        City[] temp = new City[cities.length];
        for (int i = 0; i < cities.length; i++) {
            temp[i] = cities[i];
        }
        return temp;
    }

    /**
     * Helper method to print an array of cities
     * @param cities
     */
    public static void printArray(City[] cities){
        for (City city : cities) {
            System.out.print(city.getName() + " ");
        }
    }
}
