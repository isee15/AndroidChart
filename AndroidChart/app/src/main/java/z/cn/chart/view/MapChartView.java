package z.cn.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.Random;

import z.cn.chart.data.MapChartData;
import z.cn.chart.data.MapFeature;
import z.cn.chart.data.PointDouble;

/**
 * Created by isee15 on 2016/12/24.
 */

public class MapChartView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private DrawThread drawThread;
    private boolean isSurfaceViewCreated;


    public MapChartView(Context context) {
        super(context);
        this.init();
    }

    public MapChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public MapChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

//    public MapChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        this.init();
//    }

    private void init() {
        holder = this.getHolder();
        holder.addCallback(this);
        drawThread = new DrawThread(holder);
    }

    public void setDataSource(int mapId) {
        if (!this.drawThread.isAlive() && isSurfaceViewCreated) {
            this.drawThread = new DrawThread(holder);
            drawThread.dataSource = MapChartData.getMapPaths(this.getContext(), mapId);
            this.drawThread.isRun = true;
            this.drawThread.start();
        }
        else {
            drawThread.dataSource = MapChartData.getMapPaths(this.getContext(), mapId);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.drawThread.isRun = true;
        this.drawThread.start();
        isSurfaceViewCreated = true;
    }

    /**
     * This is called immediately after any structural changes (format or
     * size) have been made to the surface.  You should at this point update
     * the imagery in the surface.  This method is always called at least
     * once, after {@link #surfaceCreated}.
     *
     * @param holder The SurfaceHolder whose surface has changed.
     * @param format The new PixelFormat of the surface.
     * @param width  The new width of the surface.
     * @param height The new height of the surface.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * This is called immediately before a surface is being destroyed. After
     * returning from this call, you should no longer try to access this
     * surface.  If you have a rendering thread that directly accesses
     * the surface, you must ensure that thread is no longer touching the
     * Surface before returning from this function.
     *
     * @param holder The SurfaceHolder whose surface is being destroyed.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.drawThread.isRun = false;
    }

    class DrawThread extends Thread {

        public boolean isRun;
        public List<MapFeature> dataSource;

        private SurfaceHolder holder;

        public DrawThread(SurfaceHolder holder) {
            this.holder = holder;
            isRun = true;
        }


        @Override
        public void run() {

            {
                Canvas c = null;
                synchronized (holder) {
                    c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
                    if (c != null) {
                        c.drawColor(Color.WHITE);//设置画布背景颜色
                    }
                    holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。
                }
            }

            double minx = Integer.MAX_VALUE;
            double maxx = Integer.MIN_VALUE;
            double miny = Integer.MAX_VALUE;
            double maxy = Integer.MIN_VALUE;
            int total = 0;
            if (dataSource != null) {
                for (MapFeature feature : dataSource) {
                    for (List<PointDouble> coordinate : feature.getGeometry()) {
                        total = Math.max(total, coordinate.size());
                        for (PointDouble point : coordinate) {
                            minx = Math.min(point.x, minx);
                            maxx = Math.max(point.x, maxx);
                            miny = Math.min(point.y, miny);
                            maxy = Math.max(point.y, maxy);
                        }
                    }
                }
            }
            if (isRun && this.dataSource != null) {
                Canvas c = null;
                synchronized (holder) {
                    try {
                        c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
                        if (c != null) {
                            c.save();                            // need to restore after drawing
                            c.translate(0, c.getHeight());  // reset where 0,0 is located
                            c.scale(1, -1);                      // invert
                            c.drawColor(Color.WHITE);//设置画布背景颜色
                            // draw to canvas here
                            Paint p = new Paint(); //创建画笔
                            p.setAntiAlias(true);

                            double scalex = c.getWidth() / (maxx - minx);
                            double scaley = c.getHeight() / (maxy - miny);
                            scalex = Math.min(scalex, scaley);
                            scaley = Math.min(scalex, scaley);
                            double offsetx = (c.getWidth() - scalex * (maxx - minx)) / 2;
                            double offsety = (c.getHeight() - scaley * (maxy - miny)) / 2;
                            for (MapFeature feature : dataSource) {
                                for (List<PointDouble> coordinate : feature.getGeometry()) {
                                    Path path = new Path();
                                    path.moveTo((float) ((coordinate.get(0).x - minx) * scalex + offsetx), (float) ((coordinate.get(0).y - miny) * scaley + offsety));

                                    for (int i = 0; i < coordinate.size(); i++) {
                                        path.lineTo((float) ((coordinate.get(i).x - minx) * scalex + offsetx), (float) ((coordinate.get(i).y - miny) * scaley + offsety));
                                    }
                                    path.close();

                                    p.setStyle(Paint.Style.FILL);
                                    p.setStrokeWidth(1);
                                    Random rnd = new Random();
                                    int[] colors = {0x87cefa,Color.YELLOW,0xFF4500};
                                    p.setColor(MapChartData.intToColor(rnd.nextInt(1500) + rnd.nextInt(1000),colors,0,2500));
                                    //p.setColor(Color.argb(128, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                                    c.drawPath(path, p);
                                    p.setStyle(Paint.Style.STROKE);
                                    p.setStrokeWidth(1);
                                    p.setColor(Color.BLACK);
                                    c.drawPath(path, p);
                                }
                            }
                            c.restore();                         // restore to normal
                            if (dataSource.size() < 64) {
                                for (MapFeature feature : dataSource) {
                                    Log.v("Map", feature.getName());
                                    if (feature.getCp() != null) {
                                        p.setColor(Color.BLACK);
                                        String text = feature.getName();
                                        p.setStyle(Paint.Style.FILL);
                                        p.setTextSize(18);
                                        c.drawText(text, (float) ((feature.getCp().x - minx) * scalex + offsetx) - p.getTextSize() * text.length() / 2, (float) (c.getHeight() - ((feature.getCp().y - miny) * scaley + offsety)) - (p.descent() + p.ascent()) / 2, p);
                                    }

                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (c != null) {
                            holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。
                        }
                    }
                }
            }
        }
    }
}
