package sustain.metadata;

import sustain.metadata.mongodb.Connector;
import sustain.metadata.utility.exceptions.ValueNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

public class PDF {

    // index = value - minVal
    // value = index + minVal

    private static DecimalFormat df = new DecimalFormat("0.00");
    // dams_geo number of records 14603

    public static void main(String[] args) {

        // we need to use the floor value of the original min and original max
        int MAX_DRAIN_AREA = 4007520;
        int MIN_DRAIN_AREA = 0;

        int MAX_DAM_HEIGHT = 770;
        int MIN_DAM_HEIGHT = -40540;

        int[] drain_area = new int[MAX_DRAIN_AREA - MIN_DRAIN_AREA + 1];
        Arrays.fill(drain_area, 0);
        int[] dam_height = new int[MAX_DAM_HEIGHT - MIN_DAM_HEIGHT + 1];
        Arrays.fill(dam_height, 0);

        double[] drain_area_prob = new double[MAX_DRAIN_AREA - MIN_DRAIN_AREA + 1];
        double[] dam_height_prob = new double[MAX_DAM_HEIGHT - MIN_DAM_HEIGHT + 1];
        Arrays.fill(drain_area_prob, 0);
        Arrays.fill(dam_height_prob, 0);

        double[] drain_area_cpd = new double[MAX_DRAIN_AREA - MIN_DRAIN_AREA + 1];
        double[] dam_height_cpd = new double[MAX_DAM_HEIGHT - MIN_DAM_HEIGHT + 1];

        try {
            Connector connector = new Connector();
            int size = connector.readDamData(drain_area, MIN_DRAIN_AREA , dam_height, MIN_DAM_HEIGHT);

            for (int i = 0; i < drain_area.length; i++) {

                double value = (double)drain_area[i]/(double)size * 100.0;
                BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN);
                drain_area_prob[i] = bd.doubleValue();

//                if(i<100)
//                    System.out.println(drain_area_prob[i]);
            }

            System.out.println("Dam Height");

            for (int i = 0; i < dam_height.length; i++) {

                double value = (double)dam_height[i]/(double)size * 100.0;
                BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN);
                dam_height_prob[i] = bd.doubleValue();

//                if(dam_height_prob[i] != 0.0d)
//                    System.out.println(i + " " + dam_height[i] + " " +dam_height_prob[i]);
            }

            for (int i = 0; i < drain_area_prob.length; i++) {
                BigDecimal bd ;
                double val;
                if(i-1 >= 0) {
                    val = drain_area_cpd[i - 1] + drain_area_prob[i];
                }
                else
                {
                    val = drain_area_prob[i];
                }
                bd = new BigDecimal(val).setScale(2, RoundingMode.HALF_DOWN);
                drain_area_cpd[i] = bd.doubleValue();
//                if(i > drain_area_prob.length-100 && i < drain_area_prob.length)
//                    System.out.println(drain_area_cpd[i]);
            }

//            System.out.println(drain_area_cpd[drain_area_cpd.length-1]);

            for (int i = 0; i < dam_height_prob.length; i++) {
                BigDecimal bd ;
                double val;
                if(i-1 >= 0)
                     val =  dam_height_cpd[i-1] + dam_height_prob[i];
                else
                    val =  dam_height_prob[i];

                bd = new BigDecimal(val).setScale(2, RoundingMode.HALF_DOWN);
                dam_height_cpd[i] = bd.doubleValue();
            }

//            System.out.println(drain_area_cpd[dam_height_cpd.length-1]);
        } catch (ValueNotFoundException e) {
            e.printStackTrace();
        }


    }
}
