package work.wanghao.youthidere;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";


    private EditText nameText;
    private EditText editText;
    private EditText passwordText;
    private Button signupButton;
    private TextView loginLink;

    private void initView() {
        nameText = (EditText) findViewById(R.id.activity_signup_input_name);
        editText = (EditText) findViewById(R.id.activity_signup_input_email);
        passwordText = (EditText) findViewById(R.id.activity_signup_input_password);
        signupButton = (Button) findViewById(R.id.activity_signup_btn_signup);
        loginLink = (TextView) findViewById(R.id.activity_signup_link_login);

        getSupportActionBar().hide();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.ActivityTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.actvity_signup_string_signuping_msg));
        progressDialog.show();

        String name = nameText.getText().toString();
        String email = editText.getText().toString();
        String password = passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.actvity_signup_string_signup_fail_msg, Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK,new Intent().putExtra("closeActivity",true));
        finish();
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = editText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError(getString(R.string.actvity_signup_string_username_input_error));
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText.setError(getString(R.string.actvity_signup_string_email_address_input_error));
            valid = false;
        } else {
            editText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(getString(R.string.actvity_signup_string_password_input_error));
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}