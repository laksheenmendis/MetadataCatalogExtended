package sustain.metadata;

import sustain.metadata.schema.Hospital;
import sustain.metadata.utility.Geohash;
import sustain.metadata.utility.exceptions.InvalidArgument;

/**
 * Created by laksheenmendis on 11/27/20 at 8:17 PM
 */
public class Test {

    public static void main(String[] args) {
//
//        System.out.println(Geohash.encode(34.09639135700007, -118.32523487099996, 2));
//        try {
//            System.out.println(Geohash.neighbours("9q"));
//        } catch (InvalidArgument invalidArgument) {
//            invalidArgument.printStackTrace();
//        }

        Hospital h = new Hospital();
        h.setBeds(4);

        System.out.println(h.getBedsString());

    }

    private static int genHash(String word)
    {
        int hash = 7;
        for (int i = 0; i < word.length(); i++) {
            hash = hash*31 + word.charAt(i);
        }

        return hash;
    }
}
