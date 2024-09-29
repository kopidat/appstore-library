package skimp.store.lib.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import skimp.store.lib.LibHelper;
import skimp.store.lib.member.SKIMP_Store_M_Lib;

public class MemberStoreAPIActivity extends AppCompatActivity {

    private static final String TAG = MemberStoreAPIActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(Bundle savedInstanceState) = " + savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_api_sample);

        // 인증 - 백그라운드 내려갔다가 다시 포그라운드 올라올때 마다 확인해야함.
        Button btn_auth = findViewById(R.id.btn_auth);
        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth();
            }
        });

        // 로그인
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        // 앱 업데이트
        Button btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        // 스토어앱 설치페이지 이동
        Button btn_go_store_install_page = findViewById(R.id.btn_go_store_install_page);
        btn_go_store_install_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goAppStoreInstallPage();
            }
        });

        // 스토어 로그인 사용자 정보 가져오기
        Button btn_get_login_user_info = findViewById(R.id.btn_get_login_user_info);
        btn_get_login_user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserInfo();
            }
        });
    }

    // 인증
    private void auth() {
        Util.showLoading(this);

        SKIMP_Store_M_Lib.getInstance().auth(this, new LibHelper.OnResultListener() {
            @Override
            public void onResult(JSONObject result) {
                Log.i(TAG, "auth.onResult(JSONObject result) = " + result);

                Util.dismissLoading();

                try {
                    String code = result.getString(LibHelper.Result.KEY_code);
                    String msg = result.getString(LibHelper.Result.KEY_msg);
                    Log.d(TAG, "code / msg = " + code + " / " + msg);
                    showResultPopup(code, msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    } // private void auth() {

    // 로그인
    private void login() {
        SKIMP_Store_M_Lib.getInstance().login(this, "my_app_scheme");
    } // private void login() {

    // 앱 업데이트
    private void update() {
        SKIMP_Store_M_Lib.getInstance().update(this, "my_app_scheme");
    } // private void update() {

    // 스토어앱 설치페이지 이동
    private void goAppStoreInstallPage() {
        SKIMP_Store_M_Lib.getInstance().goAppStoreInstallPage(this);
    } // private void goAppStoreInstallPage() {

    // 스토어 로그인 사용자 정보 가져오기
    private void getUserInfo() {
        LibHelper.LoginUserInfo userInfo = SKIMP_Store_M_Lib.getInstance().getUserInfo(this);
        Log.d(TAG, "userInfo = " + userInfo);
        if(userInfo != null) {
            Log.d(TAG, "userInfo.getUserid() = " + userInfo.getUserid());
            Log.d(TAG, "userInfo.getToken() = " + userInfo.getToken());
            Log.d(TAG, "userInfo.getEncPwd() = " + userInfo.getEncPwd());
        }
    } // private void getUserInfo() {

    private void showResultPopup(String code, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("결과");
        alertDialog.setMessage("code = " + code + "\n" + "msg = " + msg);
        alertDialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}