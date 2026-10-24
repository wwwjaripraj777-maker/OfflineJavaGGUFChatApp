package com.example.offlineaichat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private EditText messageInput;
    private Button sendButton;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private AIModel aiModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.chatRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter();
        recyclerView.setAdapter(adapter);

        aiModel = new AIModel();
        aiModel.load(this, "ai_data.json");

        sendButton.setOnClickListener(v -> {
            String userText = messageInput.getText().toString().trim();
            if (userText.isEmpty()) {
                Toast.makeText(this, "Type a message", Toast.LENGTH_SHORT).show();
                return;
            }

            adapter.addMessage(new ChatMessage(userText, true));
            String reply = aiModel.respond(userText);
            adapter.addMessage(new ChatMessage(reply, false));
            messageInput.setText("");
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        });
    }
}
