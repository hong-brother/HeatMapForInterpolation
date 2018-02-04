package interpolation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import geometry.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

public class DrawIDW implements Interpolation {
    private Polygon convexHullPoly;
    private Coordinate minX, minY, maxX, maxY;
    private double canvasHeight=0, canvasWidth=0;
    private ArrayList anyPoints;
    private ArrayList oriPoints;

    public DrawIDW(){
        anyPoints =new ArrayList();
        oriPoints =new ArrayList();
    }

    public void setCanvasSize(double height, double width){
        this.canvasHeight = height;
        this.canvasWidth = width;
    }
    public void setRandomPoint(Point[] randomPoint) throws ParseException {
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader( geometryFactory );
        for(int a = 0; a<randomPoint.length; a++){
            String wktPoint ="POINT("+randomPoint[a].x+" "+randomPoint[a].y +")";
            com.vividsolutions.jts.geom.Point Point = (com.vividsolutions.jts.geom.Point)reader.read(wktPoint);
            Object value = randomPoint[a].value;
            Point.setUserData(value);
            oriPoints.add(Point);
        }
        /*for(int b = 0; b<oriPoints.size(); b++){
            Object p  = oriPoints.get(b);
        }*/

    }
    public void setConvexHullPoly(Point[] points){
        GeometryFactory factory =new GeometryFactory();
        Coordinate[] coordinates =new Coordinate[points.length+1];
        for(int a = 0; a<points.length; a++){
            coordinates[a] = new Coordinate(points[a].x,points[a].y );
        }
        coordinates[points.length] = new Coordinate(points[0].x,points[0].y );
        convexHullPoly = (Polygon)factory.createPolygon(coordinates);
    }

    @Override
    public GraphicsContext draw(GraphicsContext gc) {
        for(int i = 0; i<anyPoints.size(); i++){
            HashMap<Integer, Double> getDisMap = new HashMap<Integer, Double>();
            for(int c = 0; c<oriPoints.size(); c++){
                com.vividsolutions.jts.geom.Point anyP = (com.vividsolutions.jts.geom.Point) anyPoints.get(i);
                com.vividsolutions.jts.geom.Point oriP = (com.vividsolutions.jts.geom.Point) oriPoints.get(c);
                double dis = anyP.distance(oriP);
                //anyPoint i에대한 거리길이
                getDisMap.put(c,dis);
            }

            HashMap<Integer, String> map = sortByValues(getDisMap);
            Set set2 = map.entrySet();

            Iterator iterator2 = set2.iterator();
            int pointIdx1=0, pointIdx2=0, pointIdx3=0;
            double pointValue1=0, pointValue2=0, pointValue3=0;
            double pointDis1=0, pointDis2=0, pointDis3=0;

            int count=0;
            while(iterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry)iterator2.next();
                if(count ==0){
                    pointIdx1 = (int) me2.getKey();
                    com.vividsolutions.jts.geom.Point P1 = (com.vividsolutions.jts.geom.Point) oriPoints.get(pointIdx1);
                    pointValue1 = (double) P1.getUserData();
                    pointDis1 = (double) me2.getValue();
                }else if(count ==1){
                    pointIdx2 = (int) me2.getKey();
                    com.vividsolutions.jts.geom.Point P2 = (com.vividsolutions.jts.geom.Point) oriPoints.get(pointIdx1);
                    pointValue2 = (double) P2.getUserData();
                    pointDis2 = (double)me2.getValue();
                }else if(count ==2){
                    pointIdx3 = (int) me2.getKey();
                    com.vividsolutions.jts.geom.Point P3 = (com.vividsolutions.jts.geom.Point) oriPoints.get(pointIdx1);
                    pointValue3 = (double) P3.getUserData();
                    pointDis3 = (double)me2.getValue();
                }
                count++;
            }//end of while


            com.vividsolutions.jts.geom.Point anyPoint =(com.vividsolutions.jts.geom.Point) anyPoints.get(i);
            double getValue = getValuePoint(anyPoint, pointDis1,pointDis2, pointDis3, pointValue1, pointValue2, pointValue3);
            System.out.println(getValue);


        }

        return gc;
    }

    @Override
    public double getValuePoint(com.vividsolutions.jts.geom.Point getPoint, double distanceP1, double distanceP2, double distanceP3,
                              double valueP1, double valueP2, double valueP3){
        int pw= 2;
        double calPar = valueP1/Math.pow(distanceP1, pw) + valueP2/Math.pow(distanceP2,pw) + valueP3/Math.pow(distanceP3,pw);
        double calSun = 1/Math.pow(distanceP1, pw) + 1/Math.pow(distanceP2,pw) + 1/Math.pow(distanceP3,pw);

        return calPar/calSun;



    }//end of getValuePoint

    @Override
    public GraphicsContext drawGrid(GraphicsContext gc) {

        gc.beginPath();
        gc.setFill(Color.BROWN);
        for(int a= 1; a<canvasWidth; a=a+20){
            for(int b = 1; b<canvasHeight; b=b+20){
                GeometryFactory geometryFactory = new GeometryFactory();
                WKTReader reader = new WKTReader( geometryFactory );

                try {
                    com.vividsolutions.jts.geom.Point gridPoint = (com.vividsolutions.jts.geom.Point)reader.read("POINT("+a+" "+ b+")");
                    if(convexHullPoly.intersects(gridPoint)){
                        anyPoints.add(gridPoint);
                        gc.fillOval(a,b,5,5);
                    }else{

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        gc.closePath();
        return gc;
    }



    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }



}
