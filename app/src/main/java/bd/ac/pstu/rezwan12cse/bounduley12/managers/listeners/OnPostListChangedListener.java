package bd.ac.pstu.rezwan12cse.bounduley12.managers.listeners;


import bd.ac.pstu.rezwan12cse.bounduley12.Model.PostListResult;

public interface OnPostListChangedListener<Post> {

    public void onListChanged(PostListResult result);

    void onCanceled(String message);
}
