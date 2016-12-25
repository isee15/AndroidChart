package z.cn.chart.data;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import z.cn.chart.R;


/**
 * Created by isee15 on 2016/12/24.
 */

public class MapChartData {

    public static List<MapFeature> getMapPaths(Context context, int mapId) {
        List<MapFeature> featureCollection = new ArrayList<>();
        InputStream inputStream = context.getResources().openRawResource(mapId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObj = JSONObject.parseObject(byteArrayOutputStream.toString());
        JSONArray features = jsonObj.getJSONArray("features");
        JSONArray jsonArr;
        for (int ii = 0; ii < features.size(); ii++) {
            jsonObj = (JSONObject) features.get(ii);
            MapFeature mapFeature = new MapFeature();
            JSONObject properties = jsonObj.getJSONObject("properties");
            mapFeature.setId(properties.getString("id"));
            mapFeature.setName(properties.getString("name"));
            JSONArray cp = properties.getJSONArray("cp");
            if (cp != null) {
                mapFeature.setCp(new PointDouble(cp.getDouble(0), cp.getDouble(1)));
            }

            List<List<PointDouble>> paths = new ArrayList<>();
            JSONObject geometry = (JSONObject) jsonObj.get("geometry");
            String type = geometry.getString("type");
            if (geometry.get("encodeOffsets") != null) {

                if ("MultiPolygon".equals(type)) {
                    jsonArr = geometry.getJSONArray("encodeOffsets");
                    jsonArr = jsonArr.getJSONArray(0);
                    List<List<Integer>> encodeOffsets = new ArrayList<>();
                    for (int i = 0; i < jsonArr.size(); i++) {
                        List<Integer> offset = new ArrayList<>();
                        JSONArray point = (JSONArray) jsonArr.get(i);
                        offset.add(point.getInteger(0));
                        offset.add(point.getInteger(1));
                        encodeOffsets.add(offset);
                    }
                    jsonArr = (JSONArray) geometry.get("coordinates");
                    jsonArr = (JSONArray) jsonArr.get(0);
                    List<String> coordinates = new ArrayList<>();
                    for (int i = 0; i < jsonArr.size(); i++) {
                        String coordinate = (String) jsonArr.get(i);
                        coordinates.add(coordinate);
                    }
                    for (int i = 0; i < coordinates.size(); i++) {
                        List<PointDouble> path = decodePolygon(coordinates.get(i), encodeOffsets.get(i));
                        paths.add(path);
                    }
                } else if ("Polygon".equals(type)) {
                    jsonArr = (JSONArray) geometry.get("encodeOffsets");
                    List<List<Integer>> encodeOffsets = new ArrayList<>();
                    for (int i = 0; i < jsonArr.size(); i++) {
                        List<Integer> offset = new ArrayList<>();
                        JSONArray point = (JSONArray) jsonArr.get(i);
                        offset.add(point.getInteger(0));
                        offset.add(point.getInteger(1));
                        encodeOffsets.add(offset);
                    }
                    jsonArr = (JSONArray) geometry.get("coordinates");
                    List<String> coordinates = new ArrayList<>();
                    for (int i = 0; i < jsonArr.size(); i++) {
                        String coordinate = (String) jsonArr.get(i);
                        coordinates.add(coordinate);
                    }
                    for (int i = 0; i < coordinates.size(); i++) {
                        List<PointDouble> path = decodePolygon(coordinates.get(i), encodeOffsets.get(i));
                        paths.add(path);
                    }
                }
            } else {
                if ("MultiPolygon".equals(type)) {
                    JSONArray mpArr = geometry.getJSONArray("coordinates");
                    for (int ci = 0; ci < mpArr.size(); ci++) {
                        jsonArr = mpArr.getJSONArray(ci).getJSONArray(0);
                        List<PointDouble> coordinates = new ArrayList<>();
                        for (int i = 0; i < jsonArr.size(); i++) {
                            JSONArray point = jsonArr.getJSONArray(i);
                            coordinates.add(new PointDouble(point.getDouble(0), point.getDouble(1)));
                        }
                        paths.add(coordinates);
                    }
                } else if ("Polygon".equals(type)) {
                    jsonArr = geometry.getJSONArray("coordinates").getJSONArray(0);
                    List<PointDouble> coordinates = new ArrayList<>();
                    for (int i = 0; i < jsonArr.size(); i++) {
                        JSONArray point = jsonArr.getJSONArray(i);
                        coordinates.add(new PointDouble(point.getDouble(0), point.getDouble(1)));
                    }
                    paths.add(coordinates);
                }
            }
            mapFeature.setGeometry(paths);
            featureCollection.add(mapFeature);

        }
        if (mapId == R.raw.china || mapId == R.raw.china_cities || mapId == R.raw.china_contour) {
            featureCollection.add(MapFeature.getNanhai());
        }
        return featureCollection;
    }


    private static List<PointDouble> decodePolygon(String coordinate, List<Integer> encodeOffsets) {
        List<PointDouble> result = new ArrayList<>();
        int prevX = encodeOffsets.get(0);
        int prevY = encodeOffsets.get(1);

        for (int i = 0; i < coordinate.length(); i += 2) {
            int x = coordinate.charAt(i) - 64;
            int y = coordinate.charAt(i + 1) - 64;
            // ZigZag decoding
            x = (x >> 1) ^ (-(x & 1));
            y = (y >> 1) ^ (-(y & 1));
            // Delta deocding
            x += prevX;
            y += prevY;

            prevX = x;
            prevY = y;
            // Dequantize
            result.add(new PointDouble(x / 1024.0f, y / 1024.0f));
        }

        return result;
    }


}
