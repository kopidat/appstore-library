package skimp.store.lib.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 정직원 스토어라이브러리 적용 샘플 페이지 이동
        Button btn_go_member_store_sample = findViewById(R.id.btn_go_member_store_sample);
        btn_go_member_store_sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MemberStoreSampleActivity.class));
            }
        });

        // 정직원 스토어라이브러리 API 샘플 페이지 이동
        Button btn_go_member_store_api_sample = findViewById(R.id.btn_go_member_store_api_sample);
        btn_go_member_store_api_sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MemberStoreAPIActivity.class));
            }
        });

        // 협력직원 스토어라이브러리 샘플 페이지 이동
        Button btn_go_partner_store_sample = findViewById(R.id.btn_go_partner_store_sample);
        btn_go_partner_store_sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PartnerStoreSampleActivity.class));
            }
        });

        // 협력직원 스토어라이브러리 API 샘플 페이지 이동
        Button btn_go_partner_store_api_sample = findViewById(R.id.btn_go_partner_store_api_sample);
        btn_go_partner_store_api_sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PartnerStoreAPIActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)");
        Log.e(TAG, "permissions = " + Arrays.toString(permissions));
        Log.e(TAG, "grantResults = " + Arrays.toString(grantResults));
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}