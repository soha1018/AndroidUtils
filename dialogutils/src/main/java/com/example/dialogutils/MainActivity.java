package com.example.dialogutils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dialogutils.dialog.DefaultDialogFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_popup = (Button) findViewById(R.id.btn_popup);
        btn_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DefaultDialogFragment dialogFragment = new DefaultDialogFragment();
                dialogFragment.setTouchOutside(true);
                View inflate = View.inflate(getApplicationContext(), R.layout.dialog_default, null);
                Button ok = inflate.findViewById(R.id.positive_button);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogFragment.dismiss();
                    }
                });
                dialogFragment.setContentView(inflate);
                dialogFragment.show(getSupportFragmentManager(),"dialogFragment");
            }
        });
    }
}
