package com.example.desespero2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class Pop extends Activity {

    public EditText nomeMarcacao;
    public Button btnOk;
    public String teste;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);


        nomeMarcacao = findViewById(R.id.edtNome1);
        btnOk = findViewById(R.id.btnSalvar);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int) (height*.6) );



    }

    public void salvar(View view) {
        Post post = new Post();

        nomeMarcacao.getText();
        teste = nomeMarcacao.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("teste", teste);
        setResult(RESULT_OK, intent);
    }

}
