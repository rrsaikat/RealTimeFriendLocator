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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import bd.ac.pstu.rezwan12cse.bounduley12.Constants;
import bd.ac.pstu.rezwan12cse.bounduley12.Model.Profile;
import bd.ac.pstu.rezwan12cse.bounduley12.R;
import bd.ac.pstu.rezwan12cse.bounduley12.managers.DatabaseHelper;
import bd.ac.pstu.rezwan12cse.bounduley12.managers.ProfileManager;
import bd.ac.pstu.rezwan12cse.bounduley12.managers.listeners.OnObjectExistListener;
import bd.ac.pstu.rezwan12cse.bounduley12.utils.LogUtil;
import bd.ac.pstu.rezwan12cse.bounduley12.utils.PreferencesUtil;


/*
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
*/

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int SIGN_IN_GOOGLE = 9001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public GoogleApiClient mGoogleApiClient;

   // private CallbackManager mCallbackManager;
    private String profilePhotoUrlLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Configure Google Sign In
        //mGoogleApiClient = GoogleApiHelper.createGoogleApiClient(this);
        findViewById(R.id.googleSignInButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        // Configure firebase auth
        mAuth = FirebaseAuth.getInstance();
/*
        if (mAuth.getCurrentUser() != null) {
            LogoutHelper.signOut(mGoogleApiClient, this);
        }
*/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Profile is signed in
                    LogUtil.logDebug(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    checkIsProfileExist(user.getUid());
                } else {
                    // Profile is signed out
                    LogUtil.logDebug(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new  GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
/*
        // Configure Facebook  Sign In
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LogUtil.logDebug(TAG, "facebook:onSuccess:" + loginResult);
                profilePhotoUrlLarge = String.format(getString(R.string.facebook_large_image_url_pattern),
                        loginResult.getAccessToken().getUserId());
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                LogUtil.logDebug(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtil.logError(TAG, "facebook:onError", error);
                showSnackBar(error.getMessage());
            }
        });

        findViewById(R.id.facebookSignInButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithFacebook();
            }
        });
*/
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                showProgress();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                profilePhotoUrlLarge = String.format(getString(R.string.google_large_image_url_pattern),
                        account.getPhotoUrl(), Constants.Profile.MAX_AVATAR_SIZE);
                firebaseAuthWithGoogle(account);
            } else {
                LogUtil.logDebug(TAG, "SIGN_IN_GOOGLE failed :" + result);
                // Google Sign In failed, update UI appropriately
                hideProgress();
            }
        }
    }

    private void checkIsProfileExist(final String userId) {
        ProfileManager.getInstance(this).isProfileExist(userId, new OnObjectExistListener<Profile>() {
            @Override
            public void onDataChanged(boolean exist) {
                if (!exist) {
                    startCreateProfileActivity();
                } else {
                    PreferencesUtil.setProfileCreated(LoginActivity.this, true);
                    DatabaseHelper.getInstance(LoginActivity.this.getApplicationContext())
                            .addRegistrationToken(FirebaseInstanceId.getInstance().getToken(), userId);
                }
                hideProgress();
                finish();
            }
        });
    }

    private void startCreateProfileActivity() {
        Intent intent = new Intent(LoginActivity.this, CreateProfileActivity.class);
        intent.putExtra(CreateProfileActivity.LARGE_IMAGE_URL_EXTRA_KEY, profilePhotoUrlLarge);
        startActivity(intent);
    }
/*
    private void handleFacebookAccessToken(AccessToken token) {
        LogUtil.logDebug(TAG, "handleFacebookAccessToken:" + token);
        showProgress();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LogUtil.logDebug(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            handleAuthError(task);
                        }
                    }
                });
    }
     */

    private void handleAuthError(Task<AuthResult> task) {
        Exception exception = task.getException();
        LogUtil.logError(TAG, "signInWithCredential", exception);

        if (exception != null) {
            showWarningDialog(exception.getMessage());
        } else {
            showSnackBar(R.string.error_authentication);
        }

        hideProgress();
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        LogUtil.logDebug(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgress();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LogUtil.logDebug(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            showSnackBar("Authentication Successfull");
                        }else{
                            handleAuthError(task);
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        LogUtil.logDebug(TAG, "onConnectionFailed:" + connectionResult);
        showSnackBar(R.string.error_google_play_services);
        hideProgress();
    }

    private void signInWithGoogle() {
        if (hasInternetConnection()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, SIGN_IN_GOOGLE);
        } else {
            showSnackBar(R.string.internet_connection_failed);
        }
    }
/*
    private void signInWithFacebook() {
        if (hasInternetConnection()) {
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
        } else {
            showSnackBar(R.string.internet_connection_failed);
        }
    }
    */
}

