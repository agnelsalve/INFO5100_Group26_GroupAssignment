
package edu.univ.util;

import edu.univ.model.GradeRecord;
import java.util.List;

public class GpaUtil {
    public static double termGpa(List<GradeRecord> recs){
        double qp = 0, cr = 0;
        for (var r: recs){
            if (!r.getLetter().equals("IP")){
                qp += GradeMap.toPoints(r.getLetter()) * r.getCredits();
                cr += r.getCredits();
            }
        }
        return cr == 0 ? 0.0 : qp / cr;
    }
}
