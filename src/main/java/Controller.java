import com.vividsolutions.jts.io.ParseException;
import convexhull.ConvexHull;
import geometry.Point;
import interpolation.DrawIDW;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    //http://boxfoxs.tistory.com/307
    @FXML private Canvas canvas;
    @FXML private Button createPointBtn;
    @FXML private TextField text;
    private GraphicsContext gc;
    private Point[] outPoint;
    private Point[] point; //randomPoint

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createPointBtn.setOnMouseClicked(event -> {
            int count = Integer.parseInt(text.getText().toString());
            draw(count);
        }); //Button Click Event
    }

    void draw(int count){
        gc= canvas.getGraphicsContext2D();
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.setFill(Color.BLUE);
        //
        point =new Point[count];
        //draw point
        for(int a=0; a<count; a++){
            point[a] =new Point();

            //Get Random Point
            double x = (Math.random() * 100000) % canvas.getWidth();
            double y = (Math.random() * 100000) % canvas.getHeight();
            int min =-10;
            int max =30;
            //-10도 ~ 30도 사이의 임의값
            double value = min + (Math.random()*max);

            point[a].x = x;
            point[a].y = y;
            point[a].value = value;

            //gc.strokeOval(x,y,5,5);
            gc.fillOval(x,y,5,5);
        }
        try{
            drawConvexHull(point);
            drawGrid();
            drawGridPoint();

        }catch(ParseException parse){
            System.out.println(parse);
        }



    }//end of draw(랜덤 포인트)

    void drawConvexHull(Point[] p){
        ConvexHull convexhull =new ConvexHull(p);
        outPoint = convexhull.convex_hull();

        gc.setStroke(Color.RED);
        gc.beginPath();

        for(int i=0; i<outPoint.length; i++){
            if(outPoint[i] != null){
                gc.lineTo(outPoint[i].x, outPoint[i].y);
            }
        }
        gc.lineTo(outPoint[0].x, outPoint[0].y);

        gc.stroke();
        gc.closePath();
    }//end of drawConvexHull(외각선)

    void drawGrid(){

        gc.beginPath();

        for(double a = 1; a< canvas.getWidth(); a=a+20){ //세로 라인
            gc.moveTo(a,0);
            gc.lineTo(a, canvas.getHeight());
        }

        for(double b = 1; b<canvas.getHeight(); b=b+20){
            gc.moveTo(0,b);
            gc.lineTo(canvas.getWidth(),b);
        }
        /*gc.moveTo(0,0);
        gc.lineTo(canvas.getHeight(), canvas.getWidth());*/
        gc.closePath();

        gc.setStroke(Color.BLACK);
        gc.stroke();
    }
    void drawGridPoint() throws ParseException {
        DrawIDW idw =new DrawIDW();

        idw.setCanvasSize(canvas.getHeight(), canvas.getWidth());
        idw.setRandomPoint(point);
        idw.setConvexHullPoly(outPoint);
        gc = idw.drawGrid(gc);
        idw.draw(gc);



    }




}
