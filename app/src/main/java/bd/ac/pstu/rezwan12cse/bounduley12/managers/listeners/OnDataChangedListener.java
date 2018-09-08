package bd.ac.pstu.rezwan12cse.bounduley12.managers.listeners;

import java.util.List;



public interface OnDataChangedListener<T> {

    public void onListChanged(List<T> list);
}
