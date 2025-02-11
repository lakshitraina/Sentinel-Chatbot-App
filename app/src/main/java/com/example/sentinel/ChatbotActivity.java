package com.example.sentinel;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sentinel.Adapter.MessageAdapter;
import com.example.sentinel.Model.MessageModel;
import com.example.sentinel.databinding.ActivityChatbotBinding;
import com.example.sentinel.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatbotActivity extends AppCompatActivity {

    @androidx.annotation.NonNull ActivityChatbotBinding binding;
    List<MessageModel> messageModels;
    MessageAdapter adapter;

    String url = "";
    String accessToken = "Your Key Here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messageModels = new ArrayList<>();

        // initialize adapter class
        adapter = new MessageAdapter(messageModels);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String question = binding.editText.getText().toString().trim();
                if (!question.isEmpty()) {
                    addToRecyclerView(question, MessageModel.SENT_BY_ME);
                    binding.editText.setText("");
                    binding.welcomeTxt.setVisibility(View.GONE);

                    callApi(question);
                }
            }
        });
    }

    private void addToRecyclerView(String question, String sendBy) {
        messageModels.add(new MessageModel(question, sendBy));
        adapter.notifyDataSetChanged();
        binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    public void callApi(String question) {

        // Add a placeholder message while waiting for the response
        messageModels.add(new MessageModel("Fetching data...", MessageModel.SENT_BY_GPT));

        JSONObject parameter = new JSONObject();
        try {
            // Specify the model and messages array
            parameter.put("model", "gpt-3.5-turbo");  // Use the correct model
            JSONArray messagesArray = new JSONArray();

            // Add the user input as the first message
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", question);
            messagesArray.put(message);

            parameter.put("messages", messagesArray);
            parameter.put("temperature", 0.7);  // Optional: adjust the creativity of the model
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray choicesArray = response.getJSONArray("choices");
                    String answer = choicesArray.getJSONObject(0).getJSONObject("message").getString("content");

                    getGptResponse(answer);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getGptResponse("Failed due to: " + error.toString());
                Log.e("errorMessage", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");  // Ensure content type is JSON
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getGptResponse(String answer) {
        // Remove the placeholder "Fetching data" message
        messageModels.remove(messageModels.size() - 1);
        addToRecyclerView(answer, MessageModel.SENT_BY_GPT);
    }
}