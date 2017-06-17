package com.lovejoy.baishi;

import android.R.integer;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT;
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT;

public class LoginActivity extends AppCompatActivity {
    //Keep track of the login task to ensure we can cancel it if requested.
    private LoginActivity.UserLoginTask mAuthTask;

    //UI references.
    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_login);


        //Toolbar--------------------------------------------------------------
        Toolbar bar = (Toolbar) this.findViewById(R.id.toolbar);
        bar.setTitle("登录");
        bar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        this.setSupportActionBar(bar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        bar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });


        //输入框------------------------------------------------------------------
        this.mPhoneView = (AutoCompleteTextView) this.findViewById(id.phone);

        this.mPasswordView = (EditText) this.findViewById(R.id.password);
        this.mPasswordView.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    LoginActivity.this.attemptLogin();
                    return true;
                }
                return false;
            }
        });


        //注册按钮-----------------------------------------------------------------
        Button mSignUpButton = (Button) this.findViewById(id.sign_up_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignupActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        //登录按钮-----------------------------------------------------------------
        Button mSignInButton = (Button) this.findViewById(id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.attemptLogin();
            }
        });


        //获取登陆表格和等待界面的View
        this.mLoginFormView = this.findViewById(id.login_form);
        this.mProgressView = this.findViewById(id.login_progress);
    }


    private void attemptLogin() {
        if (this.mAuthTask != null) {
            return;
        }

        // Reset errors.
        this.mPhoneView.setError(null);
        this.mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phone = this.mPhoneView.getText().toString();
        String password = this.mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            this.showProgress(true);

            this.mAuthTask = new LoginActivity.UserLoginTask(phone, password);
            this.mAuthTask.execute((Void) null);
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
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

            this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            this.mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class UserLoginTask extends AsyncTask<Void, Boolean, Boolean> {

        private final String mPhone;
        private final String mPassword;
        private int tag;
        //Tag==-3 连接错误
        //Tag==-2 手机号不正确
        //Tag==-1 密码不正确
        //Tag>0 登录成功


        UserLoginTask(String phone, String password) {
            this.mPhone = phone;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(2000);//不然我的进度条不就白做了嘛

                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CONNECTION_TIMEOUT, 5000);
                httpClient.getParams().setParameter(SO_TIMEOUT, 5000);
                HttpPost httpRequst = new HttpPost("http://123.206.44.12:8800/tp3.2/index.php/home/AllCommands/login");
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("phone", this.mPhone));
                paramList.add(new BasicNameValuePair("password", this.mPassword));
                httpRequst.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(httpRequst);

                if (response.getStatusLine().getStatusCode() == 200) {//连接成功返回码200
                    StringBuilder builder = new StringBuilder();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
                        builder.append(s);
                    }
                    buffer.close();
                    return this.parseJSON(builder.toString());
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
            LoginActivity.this.mAuthTask = null;
            LoginActivity.this.showProgress(false);

            if (success) {
                //MainActivity登录状态改为false 以便重新加载
                MainActivity.isUserInfoLoaded = false;
                Intent intent = new Intent(LoginActivity.this.getBaseContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LoginActivity.this.getBaseContext().startActivity(intent);
            } else if (this.tag <= -3) {
                LoginActivity.this.mPhoneView.setError("连接服务器错误");
                LoginActivity.this.mPhoneView.requestFocus();
            } else if (this.tag == -2) {
                LoginActivity.this.mPhoneView.setError("手机号不存在");
                LoginActivity.this.mPhoneView.requestFocus();
            } else if (this.tag == -1) {
                LoginActivity.this.mPasswordView.setError("密码不正确");
                LoginActivity.this.mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            LoginActivity.this.mAuthTask = null;
            LoginActivity.this.showProgress(false);
        }

        private Boolean parseJSON(String json) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = Integer.parseInt(jsonObject.getString("id"));
                    this.tag = id;
                    if (id > 0) {
                        String name = jsonObject.getString("name");
                        String imgUrl = jsonObject.getString("imgurl");
                        String labels = jsonObject.getString("labels");
                        //存储到SharedPreferences文件中
                        LoginActivity.this.savePersonalInfo(id, name, this.mPhone, imgUrl, labels);
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                this.tag = -3;
                return false;
            }
            return true;
        }
    }

    private void savePersonalInfo(int id, String name, String phone, String imgUrl, String labels) {
        SharedPreferences pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putInt("id", id);
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("imgUrl", imgUrl);
        editor.putString("labels", labels);
        editor.commit();
        return;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
    }
}

