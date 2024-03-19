package com.example.probler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
public class Home extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView imageView, scanner;
    NavigationView nav;
    ImageView profilepic;
    TextView username,txtResponse;
    Button clear, submit;
    GoogleSignInClient mGoogleSignInClient;
    EditText textbox;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawermenu);
        client = new OkHttpClient();
        imageView = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawerlayout);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        nav = findViewById(R.id.nav);
        View headerview = nav.getHeaderView(0);
        username = headerview.findViewById(R.id.uname);
        profilepic = headerview.findViewById(R.id.upropic);
        clear = findViewById(R.id.clear);
        textbox = findViewById(R.id.textbox);
        submit = findViewById(R.id.submit);
        scanner = findViewById(R.id.scanner);
        txtResponse=findViewById(R.id.txtResponse);
        textbox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // setting response tv on below line.
                txtResponse.setText("Please wait..");

                // validating text
                String question = textbox.getText().toString().trim();
                Toast.makeText(this, question, Toast.LENGTH_SHORT).show();
                if (!question.isEmpty()) {
                    getResponse(question);
                }
                return true;
            }
            return false;
        });


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            username.setText(personName);
            Glide.with(this).load(personPhoto).circleCrop().into(profilepic);
            scanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Home.this, "coming soon", Toast.LENGTH_SHORT).show();
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.open();
                }
            });
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textbox.setText("");
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(Home.this, "coming soon", Toast.LENGTH_SHORT).show();
                }
            });

            nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int option = item.getItemId();
                    if (option == R.id.lout)
                        signOut();
                    return false;
                }
            });
        }
    }


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Home.this, "signing out...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Home.this, signinActivity.class));
                        finish();
                        // ...
                    }
                });
    }





private void getResponse(String question) {
        // setting text on for question on below line.
        textbox.setText("");
        String apiKey="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";
        String url="https://api.openai.com/v1/completions";

        String requestBody = "{\n" +
        "    \"prompt\": \"" + question + "\",\n" +
        "    \"max_tokens\": 500,\n" +
        "    \"temperature\": 0\n" +
        "}";

        Request request = new Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + apiKey)
        .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
        .build();

        client.newCall(request).enqueue(new Callback() {
@Override
public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Log.e("error", "API failed", e);
        }

@Override
public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        String body = response.body().string();
        Log.v("data", body);

        try {
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray("choices");
        String textResult = jsonArray.getJSONObject(0).getString("text");
        runOnUiThread(() -> txtResponse.setText(textResult));
        } catch (JSONException e) {
        e.printStackTrace();
        }
        }
        });
        }


}

