package z.cn.chart.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by isee15 on 2016/12/25.
 */

public class MapFeature {
    private String id;
    private String name;
    private PointDouble cp;
    private List<List<PointDouble>> geometry;

    static MapFeature getNanhai() {
        final double[] geoCoord = {126, 25};

        final double[][][] points = {
                {{0, 3.5}, {7, 11.2}, {15, 11.9}, {30, 7}, {42, 0.7}, {52, 0.7},
                        {56, 7.7}, {59, 0.7}, {64, 0.7}, {64, 0}, {5, 0}, {0, 3.5}},
                {{13, 16.1}, {19, 14.7}, {16, 21.7}, {11, 23.1}, {13, 16.1}},
                {{12, 32.2}, {14, 38.5}, {15, 38.5}, {13, 32.2}, {12, 32.2}},
                {{16, 47.6}, {12, 53.2}, {13, 53.2}, {18, 47.6}, {16, 47.6}},
                {{6, 64.4}, {8, 70}, {9, 70}, {8, 64.4}, {6, 64.4}},
                {{23, 82.6}, {29, 79.8}, {30, 79.8}, {25, 82.6}, {23, 82.6}},
                {{37, 70.7}, {43, 62.3}, {44, 62.3}, {39, 70.7}, {37, 70.7}},
                {{48, 51.1}, {51, 45.5}, {53, 45.5}, {50, 51.1}, {48, 51.1}},
                {{51, 35}, {51, 28.7}, {53, 28.7}, {53, 35}, {51, 35}},
                {{52, 22.4}, {55, 17.5}, {56, 17.5}, {53, 22.4}, {52, 22.4}},
                {{58, 12.6}, {62, 7}, {63, 7}, {60, 12.6}, {58, 12.6}},
                {{0, 3.5}, {0, 93.1}, {64, 93.1}, {64, 0}, {63, 0}, {63, 92.4},
                        {1, 92.4}, {1, 3.5}, {0, 3.5}}
        };
        List<List<PointDouble>> coords = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            List<PointDouble> point = new ArrayList<>();
            for (int k = 0; k < points[i].length; k++) {
                points[i][k][0] /= 10.5;
                points[i][k][1] /= -10.5 / 0.75;
                points[i][k][0] += geoCoord[0];
                points[i][k][1] += geoCoord[1];
                point.add(new PointDouble(points[i][k][0], points[i][k][1]));
            }
            coords.add(point);
        }

        MapFeature feature = new MapFeature();
        feature.name = "南海诸岛";
        feature.cp = new PointDouble(126, 25);
        feature.geometry = coords;

        return feature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PointDouble getCp() {
        Map<String, PointDouble> coordsOffsetMap = new HashMap<>();
        coordsOffsetMap.put("南海诸岛", new PointDouble(32, 80));
        coordsOffsetMap.put("广东", new PointDouble(0, -10));
        coordsOffsetMap.put("香港", new PointDouble(10, 5));
        coordsOffsetMap.put("澳门", new PointDouble(-10, 10));
        coordsOffsetMap.put("天津", new PointDouble(5, 5));

        if (coordsOffsetMap.containsKey(name)) {
            return new PointDouble(cp.x + coordsOffsetMap.get(name).x / 10.5, cp.y - coordsOffsetMap.get(name).y / (10.5 / 0.75));
        }

        if (cp == null) {
            double minx = Integer.MAX_VALUE;
            double maxx = Integer.MIN_VALUE;
            double miny = Integer.MAX_VALUE;
            double maxy = Integer.MIN_VALUE;
            int total = 0;

            for (List<PointDouble> coordinate : this.getGeometry()) {
                total = Math.max(total, coordinate.size());
                for (PointDouble point : coordinate) {
                    minx = Math.min(point.x, minx);
                    maxx = Math.max(point.x, maxx);
                    miny = Math.min(point.y, miny);
                    maxy = Math.max(point.y, maxy);
                }
            }
            cp = new PointDouble((maxx + minx) / 2, (maxy + miny) / 2);
        }

        return cp;
    }

    public void setCp(PointDouble cp) {
        this.cp = cp;
    }

    public List<List<PointDouble>> getGeometry() {
        return geometry;
    }

    public void setGeometry(List<List<PointDouble>> geometry) {
        this.geometry = geometry;
    }


}
