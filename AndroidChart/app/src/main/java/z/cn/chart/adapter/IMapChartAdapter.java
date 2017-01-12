package z.cn.chart.adapter;

import java.util.Map;

/**
 * Created by isee15 on 2017/1/12.
 */

public interface IMapChartAdapter {
    int getResourceId();

    int[] getColorRange();

    int getValueMin();

    int getValueMax();

    Map<String, Double> getValues();
}
