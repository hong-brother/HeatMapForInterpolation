import convexhull.ConvexHull;
import geometry.Point;
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
        Point[] point =new Point[count];
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

        drawConvexHull(point);
    }

    void drawConvexHull(Point[] p){
        ConvexHull convexhull =new ConvexHull(p);
        Point[] outPoint = convexhull.convex_hull();

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


    }




}
