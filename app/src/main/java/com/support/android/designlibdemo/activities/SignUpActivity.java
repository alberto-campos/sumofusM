package com.support.android.designlibdemo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iangclifton.android.floatlabel.FloatLabel;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.support.android.designlibdemo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    // UI references.
    private FloatLabel usernameEditText;
    private FloatLabel useremail;
    private FloatLabel passwordEditText;
    private FloatLabel passwordAgainEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check Internet connection
        if (isNetworkAvailable()) {
            setContentView(R.layout.activity_sign_up);

            // Set up the signup form.
            usernameEditText = (FloatLabel) findViewById(R.id.username_edit_text);
            useremail = (FloatLabel) findViewById(R.id.useremail_edit_text);
            passwordEditText = (FloatLabel) findViewById(R.id.password_edit_text);
            passwordAgainEditText = (FloatLabel) findViewById(R.id.password_again_edit_text);


            // Set up the submit button click handler
            Button mActionButton = (Button) findViewById(R.id.action_button);
            mActionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    signup();
                }
            });
        } else {
            Toast.makeText(this, "Internet NOT Connected, please turn on your Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void signup() {
        // Validate the sign up data
        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));

        final String username = usernameEditText.getEditText().getText().toString();
        final String uemail = useremail.getEditText().getText().toString();
        final String password = passwordEditText.getEditText().getText().toString();
        final String passwordAgain = passwordAgainEditText.getEditText().getText().toString();

        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }

        if (uemail.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_useremail));
        }

        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        //ADD IMAGE Hardcoded for now. Work in Progress
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.user_image_placeholder);
        // Convert it to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        final ParseFile file = new ParseFile("profilePicture" + ".jpg", image);
        //file.saveInBackground();
        // Save the meal and return
        file.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // setResult(Activity.RESULT_OK);
                    // finish();
                    // Set up a new Parse user
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setEmail(uemail);
                    user.setPassword(password);
                    user.put("profilePicture", file);
                    user.saveInBackground();

                    // Call the Parse signup method
                    user.signUpInBackground(new

                                                    SignUpCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            dialog.dismiss();
                                                            if (e != null) {
                                                                // Show the error message
                                                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                            } else {
                                                                // Start an intent for the dispatch activity
                                                                Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                            }


                                                        }
                                                    });
                } else {

                    Log.i("SumOfUs USER info", "error during user creation");
                }
            }

        });

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.out_slide_in_left, R.anim.out_slide_out_right);
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


}