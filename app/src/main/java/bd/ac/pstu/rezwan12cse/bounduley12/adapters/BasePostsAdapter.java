

package bd.ac.pstu.rezwan12cse.bounduley12.adapters;

import android.support.v7.widget.RecyclerView;


import java.util.LinkedList;
import java.util.List;

import bd.ac.pstu.rezwan12cse.bounduley12.Model.Post;
import bd.ac.pstu.rezwan12cse.bounduley12.activities.BaseActivity;
import bd.ac.pstu.rezwan12cse.bounduley12.managers.PostManager;
import bd.ac.pstu.rezwan12cse.bounduley12.managers.listeners.OnPostChangedListener;
import bd.ac.pstu.rezwan12cse.bounduley12.utils.LogUtil;

public abstract class BasePostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = BasePostsAdapter.class.getSimpleName();

    protected List<Post> postList = new LinkedList<>();
    protected BaseActivity activity;
    protected int selectedPostPosition = -1;

    public BasePostsAdapter(BaseActivity activity) {
        this.activity = activity;
    }

    protected void cleanSelectedPostInformation() {
        selectedPostPosition = -1;
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position).getItemType().getTypeCode();
    }

    protected Post getItemByPosition(int position) {
        return postList.get(position);
    }

    private OnPostChangedListener createOnPostChangeListener(final int postPosition) {
        return new OnPostChangedListener() {
            @Override
            public void onObjectChanged(Post obj) {
                postList.set(postPosition, obj);
                notifyItemChanged(postPosition);
            }

            @Override
            public void onError(String errorText) {
                LogUtil.logDebug(TAG, errorText);
            }
        };
    }

    public void updateSelectedPost() {
        if (selectedPostPosition != -1) {
            Post selectedPost = getItemByPosition(selectedPostPosition);
            PostManager.getInstance(activity).getSinglePostValue(selectedPost.getId(), createOnPostChangeListener(selectedPostPosition));
        }
    }
}
