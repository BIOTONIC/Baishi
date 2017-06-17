package com.lovejoy.baishi;

import android.R.integer;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import com.lovejoy.baishi.R.drawable;
import com.lovejoy.baishi.R.id;
import com.lovejoy.baishi.R.layout;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT;
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT;

public class SignupActivity extends AppCompatActivity {
    //Keep track of the login task to ensure we can cancel it if requested.
    private SignupActivity.UserSignupTask mAuthTask;

    // UI references.
    private AutoCompleteTextView mNicknameView;
    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private EditText mRePasswordView;
    private View mProgressView;
    private View mSignupFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_signup);

        //Toolbar--------------------------------------------------------------
        Toolbar bar = (Toolbar) this.findViewById(id.toolbar);
        bar.setTitle("注册");
        bar.setNavigationIcon(drawable.ic_arrow_back_white_24dp);
        this.setSupportActionBar(bar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        bar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SignupActivity.this.finish();
            }
        });

        //输入框------------------------------------------------------------------
        this.mNicknameView = (AutoCompleteTextView) this.findViewById(id.nickname);
        this.mPhoneView = (AutoCompleteTextView) this.findViewById(id.phone);
        this.mPasswordView = (EditText) this.findViewById(id.password);
        this.mRePasswordView = (EditText) this.findViewById(id.repassword);

        //注册按钮-----------------------------------------------------------------
        Button mSignUpButton = (Button) this.findViewById(id.signup_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SignupActivity.this.attemptSignup();
            }
        });

        //获取登陆表格和等待界面的View-----------------------------------------------------
        this.mSignupFormView = this.findViewById(id.signup_form);
        this.mProgressView = this.findViewById(id.signup_progress);
    }


    private void attemptSignup() {
        if (this.mAuthTask != null) {
            return;
        }

        // Reset errors.
        this.mPhoneView.setError(null);
        this.mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = this.mNicknameView.getText().toString();
        String phone = this.mPhoneView.getText().toString();
        String password = this.mPasswordView.getText().toString();
        String rePassword = this.mRePasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid rePassword.
        if (!this.isRePasswordValid(password, rePassword)) {
            this.mRePasswordView.setError("两个密码不一致");
            focusView = this.mRePasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !this.isPasswordValid(password)) {
            this.mPasswordView.setError("密码长度至少要4位");
            focusView = this.mPasswordView;
            cancel = true;
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phone)) {
            this.mPhoneView.setError("手机号码不能为空");
            focusView = this.mPhoneView;
            cancel = true;
        } else if (!this.isPhoneValid(phone)) {
            this.mPhoneView.setError("手机号码长度要11位");
            focusView = this.mPhoneView;
            cancel = true;
        }

        // Check for a valid nickname.
        if (TextUtils.isEmpty(name)) {
            this.mNicknameView.setError("昵称不能为空");
            focusView = this.mNicknameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            this.showProgress(true);
            this.mAuthTask = new SignupActivity.UserSignupTask(name, phone, password);
            this.mAuthTask.execute((Void) null);
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    private boolean isRePasswordValid(String password, String rePassword) {
        return rePassword.equals(password);
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = this.getResources().getInteger(integer.config_shortAnimTime);

            this.mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            this.mSignupFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    SignupActivity.this.mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    SignupActivity.this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class UserSignupTask extends AsyncTask<Void, Boolean, Boolean> {

        private final String mName;
        private final String mPhone;
        private final String mPassword;
        private int tag;
        //Tag==-3 连接错误
        //Tag==-2 手机号已存在
        //Tag==1 注册成功


        UserSignupTask(String name, String phone, String password) {
            this.mName = name;
            this.mPhone = phone;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CONNECTION_TIMEOUT, 5000);
                httpClient.getParams().setParameter(SO_TIMEOUT, 5000);
                HttpPost httpRequst = new HttpPost("http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/register");
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("Name", this.mName));
                paramList.add(new BasicNameValuePair("Phone", this.mPhone));
                paramList.add(new BasicNameValuePair("Password", this.mPassword));
                httpRequst.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(httpRequst);

                if (response.getStatusLine().getStatusCode() == 200) {//连接成功返回码200
                    StringBuilder builder = new StringBuilder();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                        builder.append(s);
                    }
                    buffer.close();
                    String result = builder.toString();
                    this.tag = Integer.parseInt(builder.toString());
                    return this.tag > 0;
                } else {
                    this.tag = -3;
                    return false;
                }
            } catch (Exception e) {
                this.tag = -3;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            SignupActivity.this.mAuthTask = null;
            SignupActivity.this.showProgress(false);

            if (success) {
                SignupActivity.this.finish();
                Log.i("Signup", "success");
            } else if (this.tag <= -3) {
                SignupActivity.this.mPhoneView.setError("连接服务器错误");
                SignupActivity.this.mPhoneView.requestFocus();
            } else if (this.tag == -2) {
                SignupActivity.this.mPhoneView.setError("手机号已存在");
                SignupActivity.this.mPhoneView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            SignupActivity.this.mAuthTask = null;
            SignupActivity.this.showProgress(false);
        }
    }
}
