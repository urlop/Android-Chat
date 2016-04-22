package com.github.nkzawa.socketio.androidchat.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.socketio.androidchat.Chat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.HomeView.Friends.FriendsActivity;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Call;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import org.json.JSONObject;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView;
    private TextView tv_singup;
    private String mUsername;
    private PreferencesManager mPreferences;
    private Socket mSocket;
    private RestClient restClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPreferences = PreferencesManager.getInstance(this);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        restClient = new RestClient();

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username_input);
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


        setupView();
        setupActions();
//        mSocket.on("login", onLogin);
//        mSocket.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        mSocket.off("login", onLogin);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void setupView(){
        tv_singup = (TextView)findViewById(R.id.tv_singup);
    }

    private void setupActions(){
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

        Log.i("LoginActivity", " mSocket="+mSocket.id());
        loginUser(mUsername,mSocket.id());
        // perform the user login attempt.
//        mSocket.emit("add user", username);
//        mSocket.emit("store client info", username);
    }

//    private Emitter.Listener onLogin = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//
//            int numUsers = 0;
//
//            JSONObject data = (JSONObject) args[0];
//            JsonParser jsonParser = new JsonParser();
//            JsonObject gsonObject = (JsonObject)jsonParser.parse(data.toString());
//            String users = gsonObject.getAsJsonArray("users").toString();
//
//            Log.d("holy", ""+users);
//            numUsers = gsonObject.get("numUsers").getAsInt();
//
//
//            Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
//            mPreferences.saveUser(mUsername,mUsername);
//            intent.putExtra("numUsers", numUsers);
//            intent.putExtra("friendsList", users);
//            finish();
//            startActivity(intent);
//        }
//    };

    private void loginUser(String username , String sockedId){

        JsonObject user = new JsonObject();
        JsonObject attributes = new JsonObject();
        attributes.addProperty("name", username);
        attributes.addProperty("socked_id", sockedId);
        user.add("user",attributes);


        restClient.getWebservices().loginUser(user, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                JsonObject me = jsonObject.get("me").getAsJsonObject();
                String users = jsonObject.get("users").getAsJsonArray().toString();
                String rooms = jsonObject.get("rooms").getAsJsonArray().toString();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}



