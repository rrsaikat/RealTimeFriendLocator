/*
 * Copyright 2017 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package bd.ac.pstu.rezwan12cse.bounduley12.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import bd.ac.pstu.rezwan12cse.bounduley12.Model.Post;
import bd.ac.pstu.rezwan12cse.bounduley12.R;
import bd.ac.pstu.rezwan12cse.bounduley12.managers.PostManager;
import bd.ac.pstu.rezwan12cse.bounduley12.managers.listeners.OnPostCreatedListener;
import bd.ac.pstu.rezwan12cse.bounduley12.utils.LogUtil;
import bd.ac.pstu.rezwan12cse.bounduley12.utils.ValidationUtil;

public class CreatePostActivity extends PickImageActivity implements OnPostCreatedListener {
    private static final String TAG = CreatePostActivity.class.getSimpleName();
    public static final int CREATE_NEW_POST_REQUEST = 11;

    protected ImageView imageView;
    protected ProgressBar progressBar;
    protected EditText titleEditText;
    protected EditText descriptionEditText;

    protected PostManager postManager;
    protected boolean creatingPost = false;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post_activity);
        appBarLayout = findViewById(R.id.appbar_post);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_post);
        setSupportActionBar(toolbar);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        postManager = PostManager.getInstance(CreatePostActivity.this);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });

        titleEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (titleEditText.hasFocus() && titleEditText.getError() != null) {
                    titleEditText.setError(null);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public ProgressBar getProgressView() {
        return progressBar;
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public void onImagePikedAction() {
        loadImageToImageView();
    }

    protected void attemptCreatePost() {
        // Reset errors.
        titleEditText.setError(null);
        descriptionEditText.setError(null);

        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError(getString(R.string.warning_empty_description));
            focusView = descriptionEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(title)) {
            titleEditText.setError(getString(R.string.warning_empty_title));
            focusView = titleEditText;
            cancel = true;
        } else if (!ValidationUtil.isPostTitleValid(title)) {
            titleEditText.setError(getString(R.string.error_post_title_length));
            focusView = titleEditText;
            cancel = true;
        }

        if (!(this instanceof EditPostActivity) && imageUri == null) {
            showWarningDialog(R.string.warning_empty_image);
            focusView = imageView;
            cancel = true;
        }

        if (!cancel) {
            creatingPost = true;
            hideKeyboard();
            savePost(title, description);
        } else if (focusView != null) {
            focusView.requestFocus();
        }
    }

    protected void savePost(String title, String description) {
        showProgress(R.string.message_creating_post);
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setAuthorId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        postManager.createOrUpdatePostWithImage(imageUri, CreatePostActivity.this, post);
    }

    @Override
    public void onPostSaved(boolean success) {
        hideProgress();

        if (success) {
            setResult(RESULT_OK);
            CreatePostActivity.this.finish();
            LogUtil.logDebug(TAG, "Post was created");
        } else {
            creatingPost = false;
            showSnackBar(R.string.error_fail_create_post);
            LogUtil.logDebug(TAG, "Failed to create a post");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.post:
                if (!creatingPost) {
                    if (hasInternetConnection()) {
                        attemptCreatePost();
                    } else {
                        showSnackBar(R.string.internet_connection_failed);
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
