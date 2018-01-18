package com.wayne.www.waynelib;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Think on 3/21/2016.
 */
public abstract class NotifyPropertyChanged {
    protected List<OnPropertyChangedListener> onPropertyChangedListeners = new ArrayList<>();

    public void addOnPropertyChangedListener(OnPropertyChangedListener listener) {
        this.onPropertyChangedListeners.add(listener);
    }

    public void removeOnPropertyChangedListener(OnPropertyChangedListener listener) {
        this.onPropertyChangedListeners.remove(listener);
    }

    /*
    Notify all registered listeners with a property changed event.
     */
    protected void notifyAllListeners(Object sender, String propertyName) {
        for (int i = 0; i < this.onPropertyChangedListeners.size(); i++) {
            this.onPropertyChangedListeners.get(i).onPropertyChanged(sender, propertyName);
        }
    }
}
