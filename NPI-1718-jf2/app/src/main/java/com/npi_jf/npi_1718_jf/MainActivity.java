package com.npi_jf.npi_1718_jf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Método que se ejecuta al pulsar el botón de Agente Conversacional
    public void initCA(View view) {
        Intent intent = new Intent(this, ConversationalAgentActivity.class);
        startActivity(intent);
    }

}
