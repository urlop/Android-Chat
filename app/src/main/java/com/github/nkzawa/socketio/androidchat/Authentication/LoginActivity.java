package com.github.nkzawa.socketio.androidchat.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.socketio.androidchat.HomeView.HomeActivity;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView;
    private TextView tv_singup;
    private String mUsername;
    private PreferencesManager mPreferences;
    private RestClient restClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPreferences = PreferencesManager.getInstance(this);
        restClient = new RestClient();

        if(mPreferences.getUserName() != null && !mPreferences.getUserName().isEmpty()){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }

        setupView();
        setupActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void setupView(){
        tv_singup = (TextView)findViewById(R.id.tv_singup);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username_input);
    }

    private void setupActions(){
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        tv_singup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateUserActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;

        loginUser(mUsername);

    }


    private void loginUser(String username){

        JsonObject user = new JsonObject();
        JsonObject attributes = new JsonObject();
        attributes.addProperty("name", username);
        user.add("user",attributes);

        restClient.getWebservices().loginUser(user, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                JsonObject me = jsonObject.get("me").getAsJsonObject();

                String name = me.get("name").getAsString();
                int userId = me.get("id").getAsInt();
                mPreferences.saveUser(userId,name);

                JsonArray jsonArray = null;
                jsonArray = jsonObject.get("users").getAsJsonArray();

                for (JsonElement jsonElement : jsonArray) {
                    JsonObject jsonObjectUser = jsonElement.getAsJsonObject();
                    User user = User.parseUser(jsonObjectUser);
                    user.save();
                }

                jsonArray = jsonObject.get("rooms").getAsJsonArray();

                for (JsonElement jsonElement : jsonArray) {
                    JsonObject jsonObjectRoom = jsonElement.getAsJsonObject();
                    Room room = Room.parseRoom(jsonObjectRoom);
                    room.save();
                }

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}



