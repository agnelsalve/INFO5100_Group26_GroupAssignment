
package edu.univ.util;

public class GradeMap {
    public static double toPoints(String letter){
        return switch(letter){
            case "A" -> 4.0;
            case "A-" -> 3.7;
            case "B+" -> 3.3;
            case "B" -> 3.0;
            case "B-" -> 2.7;
            case "C+" -> 2.3;
            case "C" -> 2.0;
            case "C-" -> 1.7;
            case "F" -> 0.0;
            default -> 0.0;
        };
    }
    public static String fromPercent(double p){
        if (p >= 93) return "A";
        if (p >= 90) return "A-";
        if (p >= 87) return "B+";
        if (p >= 83) return "B";
        if (p >= 80) return "B-";
        if (p >= 77) return "C+";
        if (p >= 73) return "C";
        if (p >= 70) return "C-";
        return "F";
    }
}
