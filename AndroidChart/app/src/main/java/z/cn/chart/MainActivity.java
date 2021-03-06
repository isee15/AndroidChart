package z.cn.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import z.cn.chart.adapter.IMapChartAdapter;
import z.cn.chart.adapter.MapChartValue;
import z.cn.chart.view.MapChartView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.map)
    MapChartView mapView;
    @BindView(R.id.spinner)
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //mapView.setDataSource(R.raw.qinghai);
        final List<String> listData = new ArrayList<>();
//        listData.add("china");
//        listData.add("beijing");
//        listData.add("world");
//        listData.add("hubei");

        Field[] fields = R.raw.class.getFields();

        for (int i = 0; i < fields.length - 1; i++) {
            String filename = fields[i].getName();
            //do your thing here
            Log.i("filename", filename);
            int rawId = getResources().getIdentifier(filename, "raw", getPackageName());
            if (rawId <= 0) {
                continue;
            }
            TypedValue value = new TypedValue();
            getResources().getValue(rawId, value, true);
            String[] s = value.string.toString().split("/");
            Log.i("filename", s[s.length - 1]);
            if (s[s.length - 1].endsWith(".json")) {
                listData.add(filename);
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listData);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = listData.get(position);
                final int resId = getResources().getIdentifier(selected,
                        "raw", getPackageName());
                //mapView.setDataSource(resId);

                mapView.setAdapter(new IMapChartAdapter() {
                    @Override
                    public int getResourceId() {
                        return resId;
                    }

                    @Override
                    public int[] getColorRange() {
                        return new int[]{0x87cefa, Color.YELLOW, 0xFF4500};
                    }

                    @Override
                    public int getValueMin() {
                        return 0;
                    }

                    @Override
                    public int getValueMax() {
                        return 1000000;
                    }

                    @Override
                    public Map<String, Double> getValues() {
                        String mResponse = "";
                        try {
                            InputStream is = MainActivity.this.getApplicationContext().getResources().openRawResource(R.raw.population_world_2010);
                            int size = is.available();
                            byte[] buffer = new byte[size];
                            is.read(buffer);
                            is.close();
                            mResponse = new String(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Map<String, Double> ret = new HashMap<>();
                        List<MapChartValue> users = JSON.parseArray(mResponse, MapChartValue.class);
                        for (MapChartValue v :
                                users) {

                            ret.put(v.getName(), v.getValue());
                        }
                        return ret;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}


