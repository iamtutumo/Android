package com.winbeelabs.schools.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.winbeelabs.schools.R;
import com.winbeelabs.schools.network.MySingleton;
import com.winbeelabs.schools.network.Network;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText editFirstname;
    private EditText editLastname;
    private EditText editEmail;
    private EditText editPassword;

    public static final String TAG = "RegisterActivity";
    private MaterialDialog progressDialog;
    private JSONObject api_parameter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .cancelable(false)
                .progress(true, 0).build();

        editFirstname = (EditText) findViewById(R.id.editFirstname);
        editLastname = (EditText) findViewById(R.id.editLastname);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        Button buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = null;
        switch (v.getId())
        {
            case R.id.buttonSignUp:
                if (isFormValid())
                {
                    api_parameter = new JSONObject();
                    try
                    {
                        api_parameter.put("email", editEmail.getText().toString().trim());
                        api_parameter.put("password", editPassword.getText().toString().trim());
                        api_parameter.put("firstname", editFirstname.getText().toString().trim());
                        api_parameter.put("lastname", editLastname.getText().toString().trim());
                    }
                    catch (JSONException e)   {}

                    RunRegisterService();
                }

                break;
            default:
                break;
        }
    }

    public void RunRegisterService()
    {
        progressDialog.show();

        JsonObjectRequest postRequest = new JsonObjectRequest
                (Request.Method.POST, Network.API_URL + "auth/register", api_parameter, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            // If the response is JSONObject instead of expected JSONArray
                            progressDialog.dismiss();
                        }

                        try
                        {
                            JSONObject data = ((JSONObject) response.get("data"));

                            SharedPreferences.Editor user = getSharedPreferences(LoginActivity.SESSION_USER, MODE_PRIVATE).edit();

                            user.putInt("id", data.getInt("id"));
                            user.putInt("logged_in", 1);
                            user.putString("firstname", data.getString("first_name"));
                            user.putString("lastname", data.getString("last_name"));
                            user.putString("email", data.getString("email"));
                            user.putString("api_key", data.getString("api_key"));

                            JsonElement jsonElement = new Gson().fromJson(data.getString("content"), JsonElement.class);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();

                            user.putString(SettingActivity.CURRENCY_SYMBOL_KEY, jsonObject.get(SettingActivity.CURRENCY_SYMBOL_KEY).getAsString());
                            user.putString(SettingActivity.ADDRESS_SYMBOL_KEY, jsonObject.get(SettingActivity.ADDRESS_SYMBOL_KEY).getAsString());

                            user.commit();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(RegisterActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        // TODO Auto-generated method stub
                        if (progressDialog != null && progressDialog.isShowing()) {
                            // If the response is JSONObject instead of expected JSONArray
                            progressDialog.dismiss();
                        }

                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null)
                        {
                            try
                            {
                                JSONObject json = new JSONObject(new String(response.data));
                                Toast.makeText(RegisterActivity.this, json.has("message") ? json.getString("message") : json.getString("error"), Toast.LENGTH_LONG).show();
                            }
                            catch (JSONException e)
                            {
                                Toast.makeText(RegisterActivity.this, R.string.error_try_again_support, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, error != null && error.getMessage() != null ? error.getMessage() : error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-API-KEY", Network.API_KEY);
                return params;
            }
        };

        // Get a RequestQueue
        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        //Used to mark the request, so we can cancel it on our onStop method
        postRequest.setTag(TAG);

        MySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    public boolean isFormValid()
    {
        boolean isValid = true;

        if (editFirstname.getText().toString().trim().length() == 0)
        {
            editFirstname.setError("Firstname required");
            isValid = false;
        } else
        {
            editFirstname.setError(null);
        }

        if (editLastname.getText().toString().trim().length() == 0)
        {
            editLastname.setError("Lastname required");
            isValid = false;
        } else
        {
            editLastname.setError(null);
        }

        if (editEmail.getText().toString().trim().length() == 0)
        {
            editEmail.setError("Email required");
            isValid = false;
        } else
        {
            editEmail.setError(null);
        }

        if (editPassword.getText().toString().trim().length() == 0)
        {
            editPassword.setError("Password required");
            isValid = false;
        } else
        {
            editPassword.setError(null);
        }

        return isValid;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        MySingleton.getInstance(this).getRequestQueue().cancelAll(TAG);
    }
}
