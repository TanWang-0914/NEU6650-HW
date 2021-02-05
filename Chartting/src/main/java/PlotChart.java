import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.opencsv.CSVReader;
import org.knowm.xchart.*;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class PlotChart {
    public static void main(String[] args) {
        long totalReqTime = 0;
        Long startTime;
        List<Integer> responseStartTime = new ArrayList<>();
        List<Integer> responseTimeList = new ArrayList<>();
        int index = 1;
        Map<Integer,Integer> map = new HashMap<>();
        Map<Integer,Integer> count = new HashMap<>();
        int[] storeNum = {32, 64, 128, 256};

        for (int snum: storeNum){
            try (CSVReader reader = new CSVReader(new FileReader("./"+snum + "part2.csv"))){
                String[] line;
                line = reader.readNext();
                line = reader.readNext();
                startTime = Long.valueOf(line[0]);

                while ((line = reader.readNext()) != null){
                    if (line.length == 4 && line[3].equals("201")){
                        int key = (int)(Long.valueOf(line[0])-startTime)/1000;
                        int resTime = Integer.parseInt(line[2]);
                        map.put(key, map.getOrDefault(key, 0)+resTime);
                        count.put(key, count.getOrDefault(key, 0)+1);
                    }
                }
                List<Integer> keyList = new ArrayList<>(map.keySet());
                Collections.sort(keyList);
                List<Integer> averageTimeList = new ArrayList<>();
                for (int k: keyList){
                    System.out.println(map.get(k)+"/" +count.get(k));
                    int averageTime = map.get(k)/count.get(k);
                    averageTimeList.add(averageTime);
                }

                for (int i = 0; i < keyList.size(); i++){
                    System.out.println(keyList.get(i) + ": " + averageTimeList.get(i));
                }
                XYChart chart = new XYChartBuilder().xAxisTitle("RunTime").yAxisTitle("AverageResponseTime").width(1000).height(300).build();
                XYSeries series = chart.addSeries("Run Time-Average Response Time",keyList,averageTimeList);
                series.setLineStyle(SeriesLines.SOLID);
                new SwingWrapper(chart);
                BitmapEncoder.saveBitmap(chart,"./"+snum + "chart", BitmapEncoder.BitmapFormat.PNG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
