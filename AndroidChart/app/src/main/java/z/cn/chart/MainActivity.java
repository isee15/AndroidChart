package z.cn.chart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

        //mapView.setDataSource(R.raw.china);
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listData);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = listData.get(position);
                int resId = getResources().getIdentifier(selected,
                        "raw", getPackageName());
                mapView.setDataSource(resId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
