package skimp.store.lib.partner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import skimp.store.lib.LibHelper;
import skimp.store.lib.R;

/**
 *
 * 로그인 요청
 * http://dev-skimp.skinnovation.com/skimp/common/api/auth/SKIMP-0002
 *
 * 인증 요청
 * http://dev-skimp.skinnovation.com/skimp/common/api/auth/SKIMP-0001
 *
 * 인증 API status code 값
 * 1000 정상 수행
 * 2000 인증 오류
 * 3000 ID 또는 비밀번호 오류
 * 4000 Major 업데이트된 App이 스토어에 배포된 상태
 * 5000 token 만료
 * 6000 token 값이 일치하지 않음
 */
public class SKIMP_Store_P_Lib {
    private static final String TAG = SKIMP_Store_P_Lib.class.getSimpleName();

    /** The authority of this content provider. */
    private static final String AUTHORITY = "skimp.partner.store.authprovider";
    /** The URI for the user login info table. */
    public static final Uri URI_LOGIN_USER = Uri.parse("content://" + AUTHORITY + "/" + LibHelper.TBL_LOGIN_USER.NAME);
    /** The URI for the TBL_KEY table. */
    public static final Uri URI_KEY = Uri.parse("content://" + AUTHORITY + "/" + LibHelper.TBL_KEY.NAME);

    // private construct
    private SKIMP_Store_P_Lib() {
        Log.i(TAG, "SKIMP_Store_P_Lib()");
    }

    private static class InnerInstanceClazz {
        private static final SKIMP_Store_P_Lib instance = new SKIMP_Store_P_Lib();
    }

    public static SKIMP_Store_P_Lib getInstance() {
        Log.i(TAG, "getInstance()");
        return InnerInstanceClazz.instance;
    }

    /** 스토어앱 스키마 */
    private static final String APP_SCHEME = "skimp_partner_store://";

    private static final String PACKAGENAME_STORE = "skimp.partner.store";
    private static final String PACKAGENAME_MDM = "com.funnnew.android.skimds";

    /**
     * <pre>
     * 개별앱 인증 요청 처리
     * 1. 스토어앱 설치여부 확인
     * 2. MDM(MDS) 설치여부 확인
     * 3. MDM(MDS) ON 확인
     * 4. 인증 요청
     * 1000 정상 수행
     * 2000 인증 오류
     * 3000 ID 또는 비밀번호 오류
     * 4000 Major 업데이트된 App이 스토어에 배포된 상태
     * 5000 token 만료
     * 6000 token 값이 일치하지 않음
     * 7000 버전 확인 오류
     * 8000 로그아웃
     * 9000 서버 오류
     * 9001 스토어앱 설치안됨
     * 9002 MDM 오류(설치안됨, 정책 ON 안됨)
     * @param context 컨텍스트
     * @param onResultListener 결과(JSON) 전달 리스너
     * {
     *     "code" : "", // String, 결과 코드
     *     "msg" : "" // String, 결과 메시지
     * }
     */
    public void auth(Context context, LibHelper.OnResultListener onResultListener) {
        Log.i(TAG, "auth(Context context)");
        Log.d(TAG, "context.getPackageName() = " + context.getPackageName());

        LibHelper.debugDeviceInfo();

        // 1. 스토어앱 설치여부 확인
        if(!LibHelper.isInstalled(context, PACKAGENAME_STORE)) {
            LibHelper.responseResult(LibHelper.Result.CODE_ERROR_STORE_NOT_INSTALLED, LibHelper.Result.MSG_ERROR_STORE_NOT_INSTALLED, onResultListener);
            return;
        }

//        // 2. MDM(MDS) 설치여부 확인
//        if(!LibHelper.isInstalled(context, PACKAGENAME_MDM)) {
//            LibHelper.responseResult(RESULT_CODE_MDM_NOT_INSTALLED, RESULT_MSG_MDM_NOT_INSTALLED, onResultListener);
//            return;
//        }
//
//        // 3. MDM(MDS) on 확인
//        if(!LibHelper.isMDMOn(context, PACKAGENAME_MDM)) {
//            LibHelper.responseResult(RESULT_CODE_MDM_NOT_ON, RESULT_MSG_MDM_NOT_ON, onResultListener);
//            return;
//        }

        // MDM 오류 통합
        if(!LibHelper.isInstalled(context, PACKAGENAME_MDM) || !LibHelper.isMDMOn(context, PACKAGENAME_MDM)) {
            LibHelper.responseResult(LibHelper.Result.CODE_ERROR_MDM, LibHelper.Result.MSG_ERROR_MDM, onResultListener);
            return;
        }

        // 4. 인증 요청
        LibHelper.requestAuth(context, LibHelper.StoreType.PARTNER, onResultListener);
    }

    /**
     * 앱스토어 설치 페이지 이동
     * @param context 컨텍스트
     */
    public void goAppStoreInstallPage(Context context) {
        String storeDownloadPageUrl = context.getString(R.string.partner_store_download_page_url);
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(storeDownloadPageUrl)));
    }

    /**
     * 앱스토어 로그인을 위해 개별앱에서 앱스토어 호출, 결과는 스키마로 개별앱을 호출하며 전달
     * @param context 컨텍스트
     * @param scheme 개별앱 스키마
     */
    public void login(Context context, String scheme) {
        Log.i(TAG, "login(Context context, String scheme)");
//        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGENAME_STORE);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_SCHEME));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(LibHelper.EXTRA_KEY_scheme, scheme);
        intent.putExtra(LibHelper.EXTRA_KEY_loginType, LibHelper.LOGIN_TYPE_login);
        context.startActivity(intent);
    }

    /**
     * 개별앱 업데이트를 위해 개별앱에서 앱스토어 호출, 결과는 스키마로 개별앱을 호출하며 전달
     * @param context 컨텍스트
     * @param scheme 개별앱 스키마
     */
    public void update(Context context, String scheme) {
        Log.i(TAG, "update(Context context, String scheme)");
//        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGENAME_STORE);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_SCHEME));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(LibHelper.EXTRA_KEY_scheme, scheme);
        intent.putExtra(LibHelper.EXTRA_KEY_loginType, LibHelper.LOGIN_TYPE_update);
        context.startActivity(intent);
    }

    /**
     * 앱스토어 로그인 사용자 정보
     * @param context 컨텍스트
     * @return 앱스토어 로그인 사용자 정보(LibHelper.LoginUserInfo)
    */
    public LibHelper.LoginUserInfo getUserInfo(Context context) {
        Log.i(TAG, "getUserInfo(Context context)");
        return LibHelper.getUserInfo(context, LibHelper.StoreType.PARTNER);
    }
}
