package au.com.eventsecretary.placing;

import au.com.eventsecretary.equestrian.event.Award;
import au.com.eventsecretary.equestrian.scoring.Placing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static au.com.eventsecretary.NumberUtility.add;

public class LogPoints {
    private static BigDecimal ZERO_000 = new BigDecimal("0.000");

    private static BigDecimal[][] logTable2 = initLog2();

    public static BigDecimal logPoints2(Placing placing, Award award) {
        int placeCountIndex = placing.getStartingCount() - 1;
        if (placeCountIndex >= logTable2.length ) {
            placeCountIndex = logTable2.length - 1;
        }

        BigDecimal[] placePoints = logTable2[placeCountIndex];

        int placeIndex = placing.getPlace() - 1;
        BigDecimal points = ZERO_000;
        for (int i = 0; i < placing.getPlaceCount(); i++) {
            int index = i + placeIndex;
            if (index >= placePoints.length) {
                index = placePoints.length - 1;
            }
            points = add(points, placePoints[index]);
        }
        if (placing.getPlaceCount() > 1) {
            points = points.divide(new BigDecimal(placing.getPlaceCount()), 3, RoundingMode.HALF_UP);
        }
        return points;
    }


    private static BigDecimal[][] initLog2() {
        BigDecimal[][] logTable = new BigDecimal[32][];
        for (int i = 1; i <= 32; i++) {
            BigDecimal[] row = new BigDecimal[32];
            logTable[i-1] = row;
            for (int j = 1; j <= i; j++) {
                double v;
                if (i == j) {
                    v = 1;
                } else {
                    double v1 = Math.log(j+1);
                    double v2 = Math.log(i+1);
                    v = v2 / v1;
                }
                row[j-1] = BigDecimal.valueOf(v).setScale(3, RoundingMode.HALF_UP);
            }
        }

//        for (BigDecimal[] bigDecimals : logTable) {
//            for (BigDecimal bigDecimal : bigDecimals) {
//                System.out.print(bigDecimal != null ? bigDecimal.toString() : "     ");
//                System.out.print(" ");
//            }
//            System.out.println();
//        }

        return logTable;
    }

}
