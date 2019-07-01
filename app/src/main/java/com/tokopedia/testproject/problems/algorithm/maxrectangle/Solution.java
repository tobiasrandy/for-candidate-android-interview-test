package com.tokopedia.testproject.problems.algorithm.maxrectangle;


import android.util.Log;

import java.util.Arrays;
import java.util.Stack;

public class Solution {
    public static int maxRect(int[][] matrix) {
        // TODO, return the largest area containing 1's, given the 2D array of 0s and 1s
        if(matrix == null || matrix.length == 0 || matrix[0].length == 0){
            return 0;
        }
        else {
            int[] line = new int[matrix[0].length];

            //initialize line with matrix[0][i]
            for (int i = 0; i < matrix[0].length; i++) {
                line[i] = matrix[0][i];
            }
            
            int result = largestMaxArea(line);

            for (int i = 1; i < matrix.length; i++) {
                updateLine(matrix, line, i);
                result = Math.max(result, largestMaxArea(line));
            }
            return result;
        }
    }

    public static int largestMaxArea(int[] line){
        if(line == null || line.length == 0) return 0;
        int len = line.length;
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;
        for(int i = 0; i <= len; i++){
            int h = i == line.length ? 0 : line[i];
            if(stack.isEmpty() || h >= line[stack.peek()]){
                stack.push(i);
            }
            else{
                int temp = stack.pop();
                maxArea = Math.max(maxArea, line[temp] * (stack.isEmpty() ? i : i - 1 - stack.peek()));
                i--;
            }
        }
        return maxArea;
    }

    public static void updateLine(int[][] matrix, int[] line, int index){
        for(int i = 0; i < matrix[0].length; i ++){
            line[i] = matrix[index][i] == 1 ? line[i] + 1 : 0;
        }
    }
}
