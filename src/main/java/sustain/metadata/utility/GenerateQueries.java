package sustain.metadata.utility;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import sustain.metadata.Constants;
import sustain.metadata.Query;

import java.util.ArrayList;
import java.util.List;

public class GenerateQueries {

    public static int noOfQueries = 100000;

    public static List<Query> getQueries()
    {
        List<Query> allQueries = new ArrayList<Query>();

        List<Query> limitedNoOfQueries = getLimitedNoOfQueries();

        for (int i = 0; i < noOfQueries/limitedNoOfQueries.size(); i++)
        {
            allQueries.addAll(limitedNoOfQueries);
        }

        return allQueries;
    }


    private static List<Query> getLimitedNoOfQueries()
    {
        List<Query> queries = new ArrayList<Query>();

        // chicago
        Query q1 = new Query(true, 500, false, null, false, null, 41.87, -87.62);
        // birmingham
        Query q2 = new Query(true, 200, true, Constants.OWNER_GOVT, false, null, 33.51, -86.81);
        // brimingham
        Query q3 = new Query(false, -1, false, null, true, Constants.STATUS_OPEN, 33.51, -86.81);
        // santa fe
        Query q4 = new Query(false,  -1, false, null, false, null, 35.68, -105.93);
        // san francisco
        Query q5 = new Query(false, -1, false, null, true, Constants.STATUS_CLOSED, 37.33, -122.41);
        // denver
        Query q6 = new Query(true, 1592, true, Constants.OWNER_PROP, true, Constants.STATUS_OPEN, 39.74, -104.99);
        // Indiana
        Query q7 = new Query(true, 1244, false, null, true, Constants.STATUS_CLOSED, 39.7898, -86.1631);
        // Corpus Christi - Texas
        Query q8 = new Query(true, 250, false, null, false, null,27.75277, -97.38919);
        // montana
        Query q9 = new Query(false, -1, true, Constants.OWNER_GOVT_FD, false, null, 46.87, -110.36);
        // austin
        Query q10 = new Query(false, -1, true, Constants.OWNER_NP, false, null, 30.26, -97.74);

        queries.add(q1);
        queries.add(q2);
        queries.add(q3);
        queries.add(q4);
        queries.add(q5);
        queries.add(q6);
        queries.add(q7);
        queries.add(q8);
        queries.add(q9);
        queries.add(q10);

        return queries;
    }

}
