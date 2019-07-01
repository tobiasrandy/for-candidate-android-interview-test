package com.tokopedia.testproject.problems.algorithm.waterJug;

import android.util.Log;

public class Solution {

    public static int minimalPourWaterJug(int jug1, int jug2, int target) {
        // TODO, return the smallest number of POUR action to do the water jug problem
        // below is stub, replace with your implementation!

        return Math.min(solution(jug1, jug2, target), solution(jug2, jug1, target));
    }

    public static int solution(int jug1, int jug2, int target){
        if(jug1 == target || jug2 == target){
            return 1;
        }

        Jug j1 = new Jug(jug1);
        Jug j2 = new Jug(jug2);

        int pour = 0;
        while(j1.currentLitre != target && j2.currentLitre != target){
            if (j1.currentLitre == 0) {
                j1.fillJug();
            }

            if (j2.currentLitre < j2.capacity) {
                j2.setCurrentLitre(j2.currentLitre + j1.pourWater(j2.getRemainingCapacity()));
                pour++;
            }

            if (j2.isJugFull()) {
                j2.emptyJug();
            }
        }
        return pour;
    }

    static class Jug {
        private int currentLitre;
        private int capacity;

        public Jug(int capacity) {
            this.currentLitre = 0;
            this.capacity = capacity;
        }

        public boolean isJugFull(){
            return currentLitre == capacity ? true : false;
        }

        public int getRemainingCapacity() {
            return capacity-currentLitre;
        }

        public void setCurrentLitre(int currentLitre) {
            this.currentLitre = currentLitre;
        }

        public void fillJug(){
            currentLitre = capacity;
        }

        public void emptyJug(){
            currentLitre = 0;
        }

        public int pourWater(int w){
            int pouredWater = 0;
            if(currentLitre <= w){
                pouredWater = currentLitre;
                emptyJug();
            }
            else{
                pouredWater = w;
                setCurrentLitre(currentLitre - w);
            }
            return pouredWater;
        }
    }
}
