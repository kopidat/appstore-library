package skimp.store.lib.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import skimp.store.lib.LibHelper;
import skimp.store.lib.member.SKIMP_Store_M_Lib;

public class MemberStoreSampleActivity extends AppCompatActivity {

    private static final String TAG = MemberStoreSampleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(Bundle savedInstanceState) = " + savedInstanceState);
        super.onCreate(savedInstanceState);
        // 백그라운드 내려갔다가 다시 포그라운드 올라올때 마다 확인해야함.
        // 따라서, onResume에서 인증 API 호출.
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();

        // 인증 - 백그라운드 내려갔다가 다시 포그라운드 올라올때 마다 확인위해 onResume에서 호출
        auth();
    }

    // 인증 - 백그라운드 내려갔다가 다시 포그라운드 올라올때 마다 확인해야함.
    private void auth() {
        Util.showLoading(this);

        SKIMP_Store_M_Lib.getInstance().auth(this, new LibHelper.OnResultListener() {
            @Override
            public void onResult(JSONObject result) {
                Log.i(TAG, "auth.onResult(JSONObject result) = " + result);

                Util.dismissLoading();

                handleResult(result);
            }
        });
    } // private void auth() {

    private void handleResult(JSONObject result) {
        try {
            String code = result.getString(LibHelper.Result.KEY_code);
            String msg = result.getString(LibHelper.Result.KEY_msg);
            Log.d(TAG, "code / msg = " + code + " / " + msg);

            showResultPopup(code, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    } // private void handleResult(JSONObject result)

    // 로그인
    private void login() {
        SKIMP_Store_M_Lib.getInstance().login(this, "my_app_scheme");
        finishAffinity();
    } // private void login() {

    // 앱 업데이트
    private void update() {
        SKIMP_Store_M_Lib.getInstance().update(this, "my_app_scheme");
        finishAffinity();
    } // private void update() {

    // 스토어앱 설치페이지 이동
    private void goAppStoreInstallPage() {
        SKIMP_Store_M_Lib.getInstance().goAppStoreInstallPage(this);
        finishAffinity();
    } // private void goAppStoreInstallPage() {

    // 스토어 로그인 사용자 정보 가져오기
    private void getUserInfo() {
        LibHelper.LoginUserInfo userInfo = SKIMP_Store_M_Lib.getInstance().getUserInfo(this);
        Log.d(TAG, "userInfo = " + userInfo);
        if (userInfo != null) {
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
                switch (code) {
                    case LibHelper.Result.CODE_SUCCESS:
                        // 성공 - 비지니스 로직 수행.
                        // 사용자 정보를 출력해본다.
                        getUserInfo();
                        break;
                    case LibHelper.Result.CODE_ERROR_STORE_NOT_INSTALLED:
                        // 안내팝업후 앱종료하고 스토어앱 설치 페이지 이동.
                        goAppStoreInstallPage();
                        break;
                    case LibHelper.Result.CODE_ERROR_UPDATE:
                        // Major 업데이트된 App이 스토어에 배포된 상태 앱종료하고 앱 업데이트를 위해 스토어앱 호출
                        update();
                        break;
                    case LibHelper.Result.CODE_ERROR_AUTH:
                    case LibHelper.Result.CODE_ERROR_ID_OR_PASSWORD:
                    case LibHelper.Result.CODE_ERROR_EXPIRE_TOKEN:
                    case LibHelper.Result.CODE_ERROR_TOKEN_NOT_MATCH:
                    case LibHelper.Result.CODE_ERROR_VERSION:
                    case LibHelper.Result.CODE_ERROR_LOGOUT:
                    case LibHelper.Result.CODE_ERROR_MDM:
                        // 오류 팝업 안내후(잠시후 다시 시도 등) 앱종료하고 로그인을 위해 스토어앱 호출.
                        login();
                        break;
                    case LibHelper.Result.CODE_ERROR_SERVER:
                    case LibHelper.Result.CODE_ERROR_NET:
                        // 오류 팝업 안내후(잠시후 다시 시도 등) 앱종료.
                        finishAffinity();
                        break;
                }
            }
        });

        alertDialog.show();
    } // private void showResultPopup(String code, String msg)

}