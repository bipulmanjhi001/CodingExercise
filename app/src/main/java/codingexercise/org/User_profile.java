package codingexercise.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_profile extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth auth;
    CircleImageView circleImageView;
    TextView p_email,p_phone,p_name;
    ImageButton logouts;
    private static final String TAG = "User_profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();

        circleImageView=(CircleImageView)findViewById(R.id.profile_img);
        p_email=(TextView)findViewById(R.id.p_email);
        p_name=(TextView)findViewById(R.id.p_name);

        logouts=(ImageButton)findViewById(R.id.logout);
        logouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        if (user != null) {
            getProviderData();
        }
        else {
            Toast.makeText(User_profile.this, "Error in connection", Toast.LENGTH_LONG).show();
        }
    }

    public void getProviderData() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String providerId = profile.getProviderId();
                String uid = profile.getUid();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName("Bipul").build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });

                String name = profile.getDisplayName();
                p_name.setText(name);
                String email = profile.getEmail();
                p_email.setText(email);
                Uri photoUrl = profile.getPhotoUrl();
                Glide.with(getApplicationContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/codingexercise-9d342.appspot.com/o/unnamed.jpg?alt=media&token=adb5775f-46c9-4bf0-9332-51b0132808ff")
                        .error(R.drawable.ic_profle)
                        .into(circleImageView);
            }
        }
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(User_profile.this,Login.class);
        startActivity(intent);
        finish();
    }

}