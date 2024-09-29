package skimp.store.lib.project;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * 유틸리티
 *  1. 로딩 프로그레스 출력
 */
public class Util {

    private static ProgressDialog progressDialog;

    public static void showLoading(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("인증중...");
        progressDialog.show();
    }

    public static void dismissLoading() {
        progressDialog.dismiss();
    }

}