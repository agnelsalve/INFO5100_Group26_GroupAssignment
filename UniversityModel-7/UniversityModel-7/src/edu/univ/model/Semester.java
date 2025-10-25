
package edu.univ.model;

import java.time.LocalDate;

public class Semester {
    private final String code;
    private final LocalDate start;
    private final LocalDate end;

    public Semester(String code, LocalDate start, LocalDate end){
        this.code = code; this.start = start; this.end = end;
    }
    public String getCode(){ return code; }
    public LocalDate getStart(){ return start; }
    public LocalDate getEnd(){ return end; }
    public boolean beforeStart(){ return java.time.LocalDate.now().isBefore(start); }
    @Override public String toString(){ return code; }
}
