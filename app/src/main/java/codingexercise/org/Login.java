package codingexercise.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import codingexercise.org.utils.ConnectivityReceiver;

public class Login extends AppCompatActivity {

    LinearLayout sign_in_button;
    EditText user_id,password;
    Dialog myDialog;
    TextView forgot;
    String user_ids,passwords;
    ImageView show,hide;
    private CheckBox rememberMe;
    private boolean isRmChecked=false;
    private static final String TAG = "Login";
    FirebaseAuth auth;
    FirebaseUser user;
    EditText add_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        sign_in_button = (LinearLayout) findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
            }
        });

        user_id = (EditText) findViewById(R.id.user_id);
        password = (EditText) findViewById(R.id.password);

        myDialog = new Dialog(Login.this);
        forgot = (TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopup(v);
            }
        });

        show = (ImageView) findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setInputType(InputType.TYPE_CLASS_TEXT);
                show.setVisibility(View.GONE);
                hide.setVisibility(View.VISIBLE);
            }
        });

        hide = (ImageView) findViewById(R.id.hide);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                show.setVisibility(View.VISIBLE);
                hide.setVisibility(View.GONE);
            }
        });

        rememberMe = findViewById(R.id.rememberme_checkbox);
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRmChecked = isChecked;
                Remeberme();
            }
        });

        SharedPreferences preferences = getSharedPreferences("Logindetail", Context.MODE_PRIVATE);
        if (!preferences.getString("user_id", "").equals("")) {
            user_id.setText(preferences.getString("user_id", ""));
            password.setText(preferences.getString("password", ""));
            rememberMe.setChecked(true);
        }
    }
        private void Remeberme(){
            user_id.setError(null);
            password.setError(null);
            user_ids = user_id.getText().toString();
            passwords = password.getText().toString();

            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(user_ids)) {
                user_id.setError(getString(R.string.error_field_required));
                focusView = user_id;
                cancel = true;
            }
            if (TextUtils.isEmpty(passwords)) {
                password.setError(getString(R.string.error_incorrect_password));
                focusView = password;
                cancel = true;
            }
            if (cancel) {
                focusView.requestFocus();
            }else{
                if (isRmChecked) {
                    SharedPreferences preferences = getSharedPreferences("Logindetail", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("user_id", user_ids);
                    editor.putString("password", passwords);
                    editor.apply();
                }else {
                    rememberMe.setChecked(false);
                }
            }
        }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
          attemptLogin();
        }else {
            message = "Slow internet access";
            color = Color.RED;
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        }
      }
            private void attemptLogin() {
            user_id.setError(null);
            password.setError(null);
            user_ids = user_id.getText().toString();
            passwords = password.getText().toString();

            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(user_ids)) {
                user_id.setError(getString(R.string.error_field_required));
                focusView = user_id;
                cancel = true;
            }
            if (TextUtils.isEmpty(passwords)) {
                password.setError(getString(R.string.error_incorrect_password));
                focusView = password;
                cancel = true;
            }
            if (cancel) {
                focusView.requestFocus();
            }else{
                SharedPreferences preferences = getSharedPreferences("Logindetail", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user_id",user_ids);
                editor.putString("password",passwords);
                editor.apply();
                setCallLoginApi();
            }
        }

        public void setCallLoginApi() {
        auth.signInWithEmailAndPassword(user_ids,passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(Login.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Login.this, User_profile.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                    }
            }
        });
        }

    public void ShowPopup(View v) {
            TextView txtclose;
            Button btnFollow;
            myDialog.setContentView(R.layout.forgot_password);
            myDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
            add_pass=(EditText) myDialog.findViewById(R.id.add_pass);
            btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newPassword = add_pass.getText().toString();
                    if (TextUtils.isEmpty(newPassword)) {
                        Toast.makeText(Login.this, "Please enter 6 digit password", Toast.LENGTH_LONG).show();
                    } else {
                        if (user != null) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Login.this, "User password updated", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        myDialog.dismiss();
                    }
                }
            });

            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });

            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
        }
}