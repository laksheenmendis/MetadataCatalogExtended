package sustain.metadata.utility;

import sustain.metadata.utility.exceptions.InvalidArgument;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laksheenmendis on 11/27/20 at 8:10 PM
 */
public class Geohash {

    private static final String base32 = "0123456789bcdefghjkmnpqrstuvwxyz"; // (geohash-specific) Base32 map

    static final Map<Character, String[]> neighbour = new HashMap<Character, String[]>();
    static final Map<Character, String[]> border = new HashMap<Character, String[]>();

    static {
        neighbour.put('n', new String[]{"p0r21436x8zb9dcf5h7kjnmqesgutwvy", "bc01fg45238967deuvhjyznpkmstqrwx"});
        neighbour.put('s', new String[]{"14365h7k9dcfesgujnmqp0r2twvyx8zb", "238967debc01fg45kmstqrwxuvhjyznp"});
        neighbour.put('e', new String[]{"bc01fg45238967deuvhjyznpkmstqrwx", "p0r21436x8zb9dcf5h7kjnmqesgutwvy"});
        neighbour.put('w', new String[]{"238967debc01fg45kmstqrwxuvhjyznp", "14365h7k9dcfesgujnmqp0r2twvyx8zb"});

        border.put('n', new String[]{"prxz","bcfguvyz" });
        border.put('s', new String[]{"028b","0145hjnp" });
        border.put('e', new String[]{"bcfguvyz","prxz" });
        border.put('w', new String[]{"0145hjnp", "028b" });
    }

    /**
     * Encodes latitude/longitude to geohash, either to specified precision or to automatically
     * evaluated precision.
     *
     * @param   {number} lat - Latitude in degrees.
     * @param   {number} lon - Longitude in degrees.
     * @param   {number} [precision] - Number of characters in resulting geohash.
     * @returns {string} Geohash of supplied latitude/longitude.
     *
     * @example
     *     const geohash = Geohash.encode(52.205, 0.119, 7); // => 'u120fxw'
     */
    public static String encode(double lat, double lon, int precision) {

        int idx = 0; // index into base32 map
        int bit = 0; // each char holds 5 bits
        boolean evenBit = true;
        String geohash = "";

        double latMin = -90, latMax = 90;
        double lonMin = -180, lonMax = 180;

        while (geohash.length() < precision) {
            if (evenBit) {
                // bisect E-W longitude
                double lonMid = (lonMin + lonMax) / 2;
                if (lon >= lonMid) {
                    idx = idx*2 + 1;
                    lonMin = lonMid;
                } else {
                    idx = idx*2;
                    lonMax = lonMid;
                }
            } else {
                // bisect N-S latitude
                double latMid = (latMin + latMax) / 2;
                if (lat >= latMid) {
                    idx = idx*2 + 1;
                    latMin = latMid;
                } else {
                    idx = idx*2;
                    latMax = latMid;
                }
            }
            evenBit = !evenBit;

            if (++bit == 5) {
                // 5 bits gives us a character: append it and start over
                geohash += base32.charAt(idx);
                bit = 0;
                idx = 0;
            }
        }

        return geohash;
    }

    /**
     * Determines adjacent cell in given direction.
     *
     * @param   geohash - Cell to which adjacent cell is required.
     * @param   direction - Direction from geohash (N/S/E/W).
     * @returns {string} Geocode of adjacent cell.
     * @throws  InvalidArgument geohash.
     */
    public static String adjacent(String geohash,String direction) throws InvalidArgument{
        // based on github.com/davetroy/geohash-js

        geohash = geohash.toLowerCase();
        direction = direction.toLowerCase();

        if (geohash.length() == 0)
            throw new InvalidArgument("Invalid geohash");
        if ("nsew".indexOf(direction) == -1)
            throw new InvalidArgument("Invalid direction");



        Character lastCh = geohash.charAt(geohash.length()-1);    // last character of hash
        String parent = geohash.substring(0, geohash.length()-1); // hash without last character

        int type = geohash.length() % 2;

        // check for edge-cases which don't share common prefix
        if (border.get(direction.charAt(0))[type].indexOf(lastCh) != -1 && parent != "") {
            parent = Geohash.adjacent(parent, direction);
        }

        // append letter for direction to parent
        return parent + base32.charAt(neighbour.get(direction.charAt(0))[type].indexOf(lastCh));
    }


    /**
     * Returns all 8 adjacent cells to specified geohash.
     *
     * @param   {string} geohash - Geohash neighbours are required of.
     * @returns {{n,ne,e,se,s,sw,w,nw: string}}
     * @throws  InvalidArgument geohash.
     */
    public static Map<String, String> neighbours(String geohash) throws InvalidArgument{

        Map<String, String> neighbours = new HashMap<String, String>();

        try {
            neighbours.put("n", Geohash.adjacent(geohash, "n"));
            neighbours.put("ne", Geohash.adjacent(Geohash.adjacent(geohash, "n"), "e"));
            neighbours.put("e",  Geohash.adjacent(geohash, "e"));
            neighbours.put("se",  Geohash.adjacent(Geohash.adjacent(geohash, "s"), "e"));
            neighbours.put("s",  Geohash.adjacent(geohash, "s"));
            neighbours.put("sw",  Geohash.adjacent(Geohash.adjacent(geohash, "s"), "w"));
            neighbours.put("w",  Geohash.adjacent(geohash, "w"));
            neighbours.put("nw",  Geohash.adjacent(Geohash.adjacent(geohash, "n"), "w"));

        } catch (InvalidArgument invalidArgument) {
            throw invalidArgument;
        }

        return neighbours;
    }

}
