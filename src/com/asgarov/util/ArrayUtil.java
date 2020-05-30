package com.asgarov.util;

import com.asgarov.tsp.City;

public class ArrayUtil {
    public static City[] copyArray(City[] cities) {
        City[] temp = new City[cities.length];
        for (int i = 0; i < cities.length; i++) {
            temp[i] = cities[i];
        }
        return temp;
    }

    public static void printArray(City[] cities){
        for (City city : cities) {
            System.out.print(city.getName() + " ");
        }
    }
}
