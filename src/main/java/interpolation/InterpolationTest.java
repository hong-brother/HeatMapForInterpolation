package interpolation;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
//ex interpolation
public class InterpolationTest {

    private List<WeightedPoint> weightedPoints = new ArrayList<WeightedPoint>();
    private List<ColorRange> colorRanges = new ArrayList<ColorRange>();
    private Point origin = new Point(0, 0);

    int power = 4;
    Random random = new Random();

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        try {
            File imageFile = new File("newImage.png");
            ImageIO.write(new InterpolationTest().getImage(1366, 768), "png",
                    imageFile);
        } catch (IOException ex) {

        }
    }//end of main


    public BufferedImage getImage(int width, int height) {

        BufferedImage bufferedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

        initialState();
        interpolateImage(bufferedImage);

        return bufferedImage;
    }

    public void addWeightedPoint(WeightedPoint p) {
        weightedPoints.add(p);
    }

    public void addColorRange(ColorRange c) {
        colorRanges.add(c);
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    private void initialState() {

        float r = 0.34f;
        int n = 10;
        int max = 101;

        for (int i = 0; i < n; i++) {
            r -= 0.3333f / n;

            colorRanges.add(new ColorRange(i * (max / n), i * (max / n)
                    + (max / n), Color.getHSBColor(r, 1f, 1f)));
        }

        /* Agregar puntos */
        weightedPoints.add(new WeightedPoint(100, 100, 1));
        weightedPoints.add(new WeightedPoint(300, 200, 0));
        weightedPoints.add(new WeightedPoint(800, 550, 100));
        weightedPoints.add(new WeightedPoint(100, 500, 50));
        weightedPoints.add(new WeightedPoint(1200, 400, 80));
        weightedPoints.add(new WeightedPoint(1100, 700, 70));
        weightedPoints.add(new WeightedPoint(820, 550, 40));
        weightedPoints.add(new WeightedPoint(900, 220, 20));
        weightedPoints.add(new WeightedPoint(600, 150, 30));
        weightedPoints.add(new WeightedPoint(900, 550, 90));

    }

    private void interpolateImage(BufferedImage bufferedImage) {

        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                bufferedImage.setRGB(i, j, getValueShepard(i, j));
            }
        }
    }

    private int getValueShepard(int i, int j) {

        double dTotal = 0.0;
        double result = 0.0;

        for (WeightedPoint p : weightedPoints) {

            double d = distance(p.getX() - origin.getX(),
                    p.getY() - origin.getY(), i, j);
            if (power != 1) {
                d = Math.pow(d, power);
            }
            d = Math.sqrt(d);
            if (d > 0.0) {
                d = 1.0 / d;
            } else { // if d is real small set the inverse to a large number
                // to avoid INF
                d = 1.e20;
            }
            result += p.getValue() * d;
            dTotal += d;
        }

        if (dTotal > 0) {
            return getColor(result / dTotal);
        } else {
            return getColor(0);
        }

    }

    private int getColor(double val) {
        for (ColorRange r : colorRanges) {
            if (val >= r.min && val < r.max) {
                return r.color.getRGB();
            }
        }
        return 0;
    }

    /**
     * Calculates the distance between two points.
     *
     * @param xDataPt
     *            the x coordinate.
     * @param yDataPt
     *            the y coordinate.
     * @param xGrdPt
     *            the x grid coordinate.
     * @param yGrdPt
     *            the y grid coordinate.
     *
     * @return The distance between two points.
     */
    private double distance(double xDataPt, double yDataPt, double xGrdPt,
                            double yGrdPt) {
        double dx = xDataPt - xGrdPt;
        double dy = yDataPt - yGrdPt;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // bufferedImage.setRGB(i, j, new Random(100).nextInt());

    public class WeightedPoint {
        int x;
        int y;
        int value;

        public WeightedPoint(int x, int y, int value) {

            this.x = x;
            this.y = y;
            this.value = value;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public class ColorRange {
        int min;
        int max;
        Color color;

        public ColorRange(int min, int max, Color color) {

            this.min = min;
            this.max = max;
            this.color = color;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

    }

}