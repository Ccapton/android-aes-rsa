package com.ccapton.android_aes_rsa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ccapton.aesrsa.aesrsa.RsaManager;

import static com.ccapton.aesrsa.constant.StringConstant.DEMO_BEAN_STR;
import static com.ccapton.aesrsa.constant.StringConstant.DEMO_ENCRYPTED_AES_KEY;
import static com.ccapton.aesrsa.constant.StringConstant.DEMO_KEY_PIRE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button encryptBtn,decryptBtn;
    TextView encryptedTv,decryptedTv;
    EditText rawEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initAction();
    }

    private void initView() {
        encryptBtn = findViewById(R.id.encryptBtn);
        decryptBtn = findViewById(R.id.decryptBtn);
        encryptedTv = findViewById(R.id.encryptedTv);
        decryptedTv = findViewById(R.id.decryptedTv);
        rawEt = findViewById(R.id.rawEt);
    }

    private void initAction() {
        encryptBtn.setOnClickListener(this);
        decryptBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.encryptBtn:
                String encryptedText = RsaManager.getInstance().save(rawEt.getText().toString(),DEMO_BEAN_STR,DEMO_ENCRYPTED_AES_KEY,DEMO_KEY_PIRE);
                encryptedTv.setText(encryptedText);
                break;
            case R.id.decryptBtn:
                String decryptedText = RsaManager.getInstance().load(DEMO_BEAN_STR,DEMO_ENCRYPTED_AES_KEY,DEMO_KEY_PIRE);
                decryptedTv.setText(decryptedText);
                break;
        }
    }
}
