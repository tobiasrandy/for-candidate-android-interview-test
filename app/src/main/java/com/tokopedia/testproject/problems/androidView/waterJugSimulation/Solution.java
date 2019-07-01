package com.tokopedia.testproject.problems.androidView.waterJugSimulation;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<WaterJugAction> simulateWaterJug(int jug1, int jug2, int target) {
        // TODO, simulate the smallest number of action to do the water jug problem
        // below is stub, replace with your implementation!
        List<WaterJugAction> list = new ArrayList<>();
        list.add(new WaterJugAction(WaterJugActionEnum.FILL, 1));
        list.add(new WaterJugAction(WaterJugActionEnum.POUR, 2));
        list.add(new WaterJugAction(WaterJugActionEnum.FILL, 1));
        list.add(new WaterJugAction(WaterJugActionEnum.POUR, 2));
        list.add(new WaterJugAction(WaterJugActionEnum.EMPTY, 2));
        list.add(new WaterJugAction(WaterJugActionEnum.POUR, 2));
        list.add(new WaterJugAction(WaterJugActionEnum.FILL, 1));
        list.add(new WaterJugAction(WaterJugActionEnum.POUR, 2));
        return solution(jug1, jug2, target, 1).size() < solution(jug2, jug1, target, 2).size() ?
                solution(jug1, jug2, target, 1) : solution(jug2, jug1, target, 2);
    }

    public static List<WaterJugAction> solution(int jug1, int jug2, int target, int type){
        List<WaterJugAction> list = new ArrayList<>();
        if(jug1 == target || jug2 == target){
            if(type == 1) {
                list.add(new WaterJugAction(WaterJugActionEnum.FILL, 1));
            }
            else {
                list.add(new WaterJugAction(WaterJugActionEnum.FILL, 2));
            }
            return list;

        }

        Jug j1 = new Jug(jug1);
        Jug j2 = new Jug(jug2);

        int s = 0;
        while(j1.currentLitre != target && j2.currentLitre != target){
            if (j1.currentLitre == 0) {
                j1.fillJug();

                if(type == 1) {
                    list.add(new WaterJugAction(WaterJugActionEnum.FILL, 1));
                }
                else {
                    list.add(new WaterJugAction(WaterJugActionEnum.FILL, 2));
                }

                s++;
            }

            if (j2.currentLitre < j2.capacity) {
                j2.setCurrentLitre(j2.currentLitre + j1.pourWater(j2.getRemainingCapacity()));

                if(type == 1) {
                    list.add(new WaterJugAction(WaterJugActionEnum.POUR, 2));
                }
                else {
                    list.add(new WaterJugAction(WaterJugActionEnum.POUR, 1));
                }

                s++;
            }

            if (j2.isJugFull() && j1.currentLitre != target && j2.currentLitre != target) {
                j2.emptyJug();

                if(type == 1) {
                    list.add(new WaterJugAction(WaterJugActionEnum.EMPTY, 2));
                }
                else {
                    list.add(new WaterJugAction(WaterJugActionEnum.EMPTY, 1));
                }

                s++;
            }
        }
        return list;
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
