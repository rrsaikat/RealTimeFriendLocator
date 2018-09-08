package bd.ac.pstu.rezwan12cse.bounduley12.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import bd.ac.pstu.rezwan12cse.bounduley12.R;


public class EditCommentDialog extends DialogFragment {
    public static final String TAG = EditCommentDialog.class.getSimpleName();
    public static final String COMMENT_TEXT_KEY = "EditCommentDialog.COMMENT_TEXT_KEY";
    public static final String COMMENT_ID_KEY = "EditCommentDialog.COMMENT_ID_KEY";

    private CommentDialogCallback callback;
    private String commentText;
    private String commentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getActivity() instanceof CommentDialogCallback) {
            callback = (CommentDialogCallback) getActivity();
        } else {
            throw new RuntimeException(getActivity().getTitle() + " should implements CommentDialogCallback");
        }

        commentText = (String) getArguments().get(COMMENT_TEXT_KEY);
        commentId = (String) getArguments().get(COMMENT_ID_KEY);

        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_edit_comment, null);

        final EditText editCommentEditText = (EditText) view.findViewById(R.id.editCommentEditText);

        if (commentText != null) {
            editCommentEditText.setText(commentText);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(R.string.title_edit_comment)
                .setNegativeButton(R.string.button_title_cancel, null)
                .setPositiveButton(R.string.button_title_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newCommentText = editCommentEditText.getText().toString();

                        if (!newCommentText.equals(commentText) && callback != null) {
                            callback.onCommentChanged(newCommentText, commentId);
                        }

                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    public interface CommentDialogCallback {
        void onCommentChanged(String newText, String commentId);
    }
}
