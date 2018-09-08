package bd.ac.pstu.rezwan12cse.bounduley12.managers.listeners;


import bd.ac.pstu.rezwan12cse.bounduley12.Model.Post;

public interface OnPostChangedListener {
    public void onObjectChanged(Post obj);

    public void onError(String errorText);
}
