package interpolation;

import com.vividsolutions.jts.geom.Point;
import javafx.scene.canvas.GraphicsContext;

import java.util.Map;

/**
 * Interpolation
 * <pre>
 * <b>History:</b>
 *    hsnam, 1.0, 2018.1.24 Create Interface
 * </pre>
 *
 * @author hsnam
 * @version 1.0
 */
public interface Interpolation {
    public GraphicsContext draw(GraphicsContext gc);

    public GraphicsContext drawGrid(GraphicsContext gc);

    public double getValuePoint(Point getPoint, double distanceP1, double distanceP2, double distanceP3, double valueP1, double valueP2, double valueP3); //보간법 실행
}
