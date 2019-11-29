package bearmaps.proj2c;

import bearmaps.hw4.WeirdSolver;
import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.proj2ab.Point;
import bearmaps.proj2ab.WeirdPointSet;
import edu.princeton.cs.algs4.TrieSET;
import edu.princeton.cs.algs4.TST;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {

    private HashMap<Point, Node> points = new HashMap<>();
    private WeirdPointSet locations;
    private TST places = new TST();
    List<Node> nodes;
    private HashMap<String, List<Node>> fullnames = new HashMap<>();

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        // You might find it helpful to uncomment the line below:
        nodes = this.getNodes();
        List<Point> points1 = new ArrayList<>();
        for (Node node : nodes) {
            if (neighbors(node.id()).size() != 0) {
                points.put(new Point(node.lon(), node.lat()), node);
                points1.add(new Point(node.lon(), node.lat()));
                }
            if (node.name() != null) {
                if (!fullnames.containsKey(cleanString(node.name()))) {
                    fullnames.put(cleanString(node.name()), new LinkedList<>());
                }
                fullnames.get(cleanString(node.name())).add(node);
                if (cleanString(node.name()).length() != 0) {
                    places.put(cleanString(node.name()), 9);
                }
            }
        }
        locations = new WeirdPointSet(points1);
    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        return points.get(locations.nearest(lon, lat)).id();
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        List<String> names = new ArrayList<>();
        Iterable<String> places1 = places.keysWithPrefix(cleanString(prefix));
        for (String name : places1) {
            for (Node fullname : fullnames.get(name)) {
               names.add(fullname.name());
            }
        }
        return names;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        for (Node node : fullnames.get(cleanString(locationName))) {
                Map<String, Object> map = new HashMap<>();
                map.put("lat", node.lat());
                map.put("lon", node.lon());
                map.put("name", node.name());
                map.put("id", node.id());
                returnList.add(map);
            }
        return returnList;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
