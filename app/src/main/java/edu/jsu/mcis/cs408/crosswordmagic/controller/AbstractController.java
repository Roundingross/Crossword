package edu.jsu.mcis.cs408.crosswordmagic.controller;

import android.util.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import edu.jsu.mcis.cs408.crosswordmagic.model.AbstractModel;
import edu.jsu.mcis.cs408.crosswordmagic.view.AbstractView;

public abstract class AbstractController implements PropertyChangeListener {

    private final ArrayList<AbstractView> views;
    private final ArrayList<AbstractModel> models;

    public AbstractController() {
        views = new ArrayList<>();
        models = new ArrayList<>();
    }

    public void addModel(AbstractModel model) {
        Log.d("DEBUG", "Controller Added Model: " + model.getClass().getSimpleName());
        models.add(model);
        model.addPropertyChangeListener(this);
    }

    public void removeModel(AbstractModel model) {
        models.remove(model);
        model.removePropertyChangeListener(this);
    }

    public void addView(AbstractView view) {
        views.add(view);
    }

    public void removeView(AbstractView view) {
        views.remove(view);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Log.d("DEBUG", "Controller Property changed: " + evt.getPropertyName());
        for (AbstractView view : views) {
            Log.d("DEBUG", "Notifying view: " + view.getClass().getSimpleName());
            view.modelPropertyChange(evt);
        }
    }

    protected void setModelProperty(String propertyName, Object newValue) {
        for (AbstractModel model : models) {
            try {
                Method method = model.getClass().getMethod("set" + propertyName, newValue.getClass());
                method.invoke(model, newValue);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void getModelProperty(String methodName) {
        for (AbstractModel model : models) {
            try {
                Method method = model.getClass().getMethod("get" + methodName);
                method.invoke(model);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}