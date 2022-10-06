package se.miun.holi1900.dt176g;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Drawing</h1>
 * Let this class store an arbitrary number of AbstractShape-objects in
 * some kind of container.
 *
 * @author 	Honorine Lima
 * @version 1.0
 * @since 	2022-09-18
 */
public class Drawing implements Drawable{
    private List<Shape> shapes = new ArrayList<>();


    /**
     * <h1>addShape</h1> add a shape to the Shapes list
     *
     * @param s a {@link Shape} object.
     */
    public void addShape(Shape s) {
        this.shapes.add(s);
    }

    /**
     * removes the last shape from the list of shapes
     */
    public void removeLastShape(){
        int size = shapes.size();
        if(size!=0){
            shapes.remove(size-1);
        }
    }


    @Override
    public void draw(Graphics g) {
        /*if(!shapes.isEmpty()){
            for (Shape shape : shapes) {
                shape.draw(g);
            }
        }*/
        Observable<Shape> shapesObservable = Observable.fromIterable(shapes);
        Disposable d = shapesObservable.subscribe(shape -> shape.draw(g));
    }
}
