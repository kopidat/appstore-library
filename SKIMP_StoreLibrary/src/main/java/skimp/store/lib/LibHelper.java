package skimp.store.lib;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import skimp.store.lib.member.SKIMP_Store_M_Lib;
import skimp.store.lib.net.RetrofitClient;
import skimp.store.lib.net.data.auth.ReqAuth;
import skimp.store.lib.net.data.auth.ResAuth;
import skimp.store.lib.partner.SKIMP_Store_P_Lib;
import skimp.store.lib.util.AES256Util;


public class LibHelper {
	private static final String TAG = LibHelper.class.getSimpleName();

	public static final String EXTRA_KEY_scheme = "scheme";
	public static final String EXTRA_KEY_loginType = "type";

	public static final String LOGIN_TYPE_login = "login";
	public static final String LOGIN_TYPE_update = "update";

	public interface Result {
		String KEY_code = "code";
		String KEY_msg = "msg";
		String CODE_SUCCESS = "1000";
		String MSG_SUCCESS = "정상 수행";
		String CODE_ERROR_AUTH = "2000";
		String MSG_ERROR_AUTH = "인증 오류";
		String CODE_ERROR_ID_OR_PASSWORD = "3000";
		String MSG_ERROR_ID_OR_PASSWORD = "ID 또는 비밀번호 오류";
		String CODE_ERROR_UPDATE = "4000";
		String MSG_ERROR_UPDATE = "Major 업데이트된 App이 스토어에 배포된 상태";
		String CODE_ERROR_EXPIRE_TOKEN = "5000";
		String MSG_ERROR_EXPIRE_TOKEN = "token 만료";
		String CODE_ERROR_TOKEN_NOT_MATCH = "6000";
		String MSG_ERROR_TOKEN_NOT_MATCH = "token 값이 일치하지 않음";
		String CODE_ERROR_VERSION = "7000";
		String MSG_ERROR_VERSION = "버전 확인 오류";
		String CODE_ERROR_LOGOUT = "8000";
		String MSG_ERROR_LOGOUT = "로그아웃";
		String CODE_ERROR_SERVER = "9000";
		String MSG_ERROR_SERVER = "서버 오류";
		String CODE_ERROR_STORE_NOT_INSTALLED = "9001";
		String MSG_ERROR_STORE_NOT_INSTALLED = "스토어앱 설치 안됨";
		String CODE_ERROR_MDM = "9002";
		String MSG_ERROR_MDM = "MDM 오류";
		String CODE_ERROR_NET = "9003";
		String MSG_ERROR_NET = "네트워크 요청 오류";
	}

	public static final class TBL_LOGIN_USER implements BaseColumns {
		public static final String NAME = "TBL_LOGIN_USER";

		/** 스토어 로그인 ID */
		public static final String COL_userId = "userId";
		/** 스토어 로그인 후 발급받은 토큰 정보 */
		public static final String COL_token = "token";
		/** 암호화된 패스워드 */
		public static final String COL_encPwd = "encPwd";
	}

	/** 암/복호화 key */
	public static final class TBL_KEY implements BaseColumns {
		public static final String NAME = "TBL_KEY";

		/** 암/복호화 key */
		public static final String COL_key = "key";
	}

	/**
	 * 앱스토어 로그인 사용자 정보
	 */
	public static class LoginUserInfo {
		/** 암호화된 사용자 아이디 */
		private String userid;
		/** 로그인후 서버에서 발급받은 토큰 */
		private String token;
		/** 암호화된 사용자 비밀번호 */
		private String encPwd;

		LoginUserInfo(String userid, String token, String encPwd) {
			this.userid = userid;
			this.token = token;
			this.encPwd = encPwd;
		}

		/**
		 * 암호화된 사용자 아이디
		 * @return 암호화된 사용자 아이디
		 */
		public String getUserid() {
			return userid;
		}

		/**
		 * 로그인후 서버에서 발급받은 토큰
		 * @return 로그인후 서버에서 발급받은 토큰
		 */
		public String getToken() {
			return token;
		}

		/**
		 * 암호화된 사용자 비밀번호
		 * @return 암호화된 사용자 비밀번호
		 */
		public String getEncPwd() {
			return encPwd;
		}
	}


	public enum StoreType {
		MEMBRER, PARTNER
	}

	public interface OnResultListener {
		void onResult(JSONObject result);
	}


	public static void requestAuth(Context context, StoreType storeType, OnResultListener onResultListener) {
		ReqAuth reqInfo = createReqAuth(context, storeType);
		Log.e(TAG, "reqInfo = " + reqInfo);
		if(reqInfo == null) {
			responseResult("-1000", "인증정보 오류", onResultListener);
			return;
		}

		Call<ResAuth> authCall = RetrofitClient.getService(context).auth(reqInfo);
		authCall.enqueue(new Callback<ResAuth>() {
			@Override
			public void onResponse(Call<ResAuth> call, Response<ResAuth> response) {
				Log.e(TAG, "authCall.onResponse(Call<ResAuth> call, Response<ResAuth> response)");
				Log.e(TAG, "call = " + call);
				Log.e(TAG, "response = " + response);
				ResAuth responseBody = response.body();
				Log.e(TAG, "responseBody = " + responseBody);

				String status;
				if(responseBody == null) {
					status = Result.CODE_ERROR_SERVER;
				} else {
					status = responseBody.getStatus();
				}

				responseResult(status, getStatusMessage(status), onResultListener);
			}

			@Override
			public void onFailure(Call<ResAuth> call, Throwable t) {
				Log.e(TAG, "authCall.onFailure(Call<ResAuth> call, Throwable t)");
				Log.e(TAG, "call = " + call);
				Log.e(TAG, "t = " + t);
				responseResult(Result.CODE_ERROR_NET, t.toString(), onResultListener);
			}
		});
	}

	public static ReqAuth createReqAuth(Context context, StoreType storeType) {
		ReqAuth reqAuth = new ReqAuth();
		// API ID, 고정값, SKIMP-0001
//		reqAuth.body.intfId = "SKIMP-0001";
		// 디바이스 명 또는 OS 명, 고정값, Android
		reqAuth.body.osName = "Android";
		// platIdx = "1" (안드로이드: 1 , IOS: 2)
		reqAuth.body.platIdx = "1";
		switch(storeType) {
			case MEMBRER:
				// 정직원여부, 정직원Y, 협력직원N
				reqAuth.body.regularityYN = "Y";
				break;
			case PARTNER:
				// 정직원여부, 정직원Y, 협력직원N
				reqAuth.body.regularityYN = "N";
				break;
		}

		// context 에서 정보 추출
		reqAuth.body.pakgId = context.getPackageName();
		reqAuth.body.binVer = getPackageVersionName(context);

		// 다바이스 고유 ID, Android
		reqAuth.body.mdn = getAndroidId(context);
		reqAuth.body.osVer = Build.VERSION.RELEASE;

		// ContentProvider를 통해 스토어앱에서 받아온다.
		LoginUserInfo loginUserInfo = getUserInfo(context, storeType);
		if(loginUserInfo == null) {
			reqAuth.body.token = "";
			reqAuth.body.encPwd = "";
		} else {
			reqAuth.body.token = loginUserInfo.token;
			reqAuth.body.encPwd = loginUserInfo.encPwd;
		}

		return reqAuth;
	}

	public static boolean isInstalled(Context context) {
		return isInstalled(context, context.getPackageName());
	}

	public static boolean isInstalled(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packageName.trim(), PackageManager.GET_META_DATA);
			ApplicationInfo appInfo = pi.applicationInfo;
			// 패키지가 있을 경우.
			Log.d(TAG,"Enabled value = " + appInfo.enabled);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			Log.d(TAG,"패키지가 설치 되어 있지 않습니다.");
			return false;
		}
	}

	public static boolean isMDMOn(Context context, String packageName) {
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		List<ComponentName> activeAdmins = devicePolicyManager.getActiveAdmins();
		if (activeAdmins != null) {
			for (ComponentName admin : activeAdmins) {
				if (admin.getPackageName().compareTo(packageName) == 0) {
					if (devicePolicyManager.getCameraDisabled(admin)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static String getAndroidId(Context context) {
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
	}

	public static String getPackageVersionName(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			Log.d(TAG, "packageInfo.versionCode = " + packageInfo.versionCode);
			Log.d(TAG, "packageInfo.versionName = " + packageInfo.versionName);
			return packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0.0";
	}

	public static String getStatusMessage(String status) {
		String statusMessage = "";
		switch (status) {
			case Result.CODE_SUCCESS:
				statusMessage = Result.MSG_SUCCESS;
				break;
			case Result.CODE_ERROR_AUTH:
				statusMessage = Result.MSG_ERROR_AUTH;
				break;
			case Result.CODE_ERROR_ID_OR_PASSWORD:
				statusMessage = Result.MSG_ERROR_ID_OR_PASSWORD;
				break;
			case Result.CODE_ERROR_UPDATE:
				statusMessage = Result.MSG_ERROR_UPDATE;
				break;
			case Result.CODE_ERROR_EXPIRE_TOKEN:
				statusMessage = Result.MSG_ERROR_EXPIRE_TOKEN;
				break;
			case Result.CODE_ERROR_TOKEN_NOT_MATCH:
				statusMessage = Result.MSG_ERROR_TOKEN_NOT_MATCH;
				break;
			case Result.CODE_ERROR_VERSION:
					statusMessage = Result.MSG_ERROR_VERSION;
				break;
			case Result.CODE_ERROR_LOGOUT:
					statusMessage = Result.MSG_ERROR_LOGOUT;
				break;
			case Result.CODE_ERROR_SERVER:
				statusMessage = Result.MSG_ERROR_SERVER;
				break;
		}
		return statusMessage;
	}

	public static void uninstall(Context context, String packageName) {
		Uri uri = Uri.fromParts("package", packageName, null);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}

//		Intent intent = new Intent();
//		PendingIntent sender = PendingIntent.getActivity(context, 0, intent, 0);
//		PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
//		packageInstaller.uninstall(packageName, sender.getIntentSender());
	}

	public static void uriQueryToJson(JSONObject destJson, Uri srcUri) {
		Log.i(TAG, "uriQueryToJson(JSONObject destJson, Uri srcUri) = " + srcUri);

		if(srcUri == null) {
			Log.w(TAG, "if(srcUri == null)");
			return;
		}

		Log.d(TAG, "srcUri.getQuery() = " + srcUri.getQuery());
		Log.d(TAG, "srcUri.getQueryParameterNames() = " + srcUri.getQueryParameterNames());
		Set<String> keys = srcUri.getQueryParameterNames();
		Log.d(TAG, "keys.size() = " + keys.size());
		for(String key : keys) {
			String value = srcUri.getQueryParameter(key);
			Log.d(TAG, "key / value = " + key + " / " + value);
			try {
				destJson.put(key, value);
			} catch (JSONException e) {
				// 안드로이드 소스코드 결과보고서.docx, 4.5. 오류메시지를 통한 정보 노출
				e.printStackTrace();
			}
		}
	}
	
	public static void debugIntent(Intent intent) {
		debugIntent(TAG, intent);
	}

	public static void debugIntent(String tag, Intent intent) {
		if(TextUtils.isEmpty(tag)) {
			tag = TAG;
		}

		Log.i(tag, "debugIntent(Intent intent) = " + intent);

		if(intent == null) {
			Log.w(tag, "if(intent == null)");
			return;
		}

		debugUri(tag, intent.getData());

		Bundle extras = intent.getExtras();
		debugBundle(tag, extras);
	}

	public static void debugUri(Uri uri) {
		debugUri(TAG, uri);
	}
	
	public static void debugUri(String tag, Uri uri) {
		if(TextUtils.isEmpty(tag)) {
			tag = TAG;
		}

		Log.i(tag, "debugUri(String tag, Uri uri) = " + uri);

		if(uri == null) {
			Log.w(tag, "if(uri == null)");
			return;
		}

		Log.d(tag, "uri.getScheme() = " + uri.getScheme());
		Log.d(tag, "uri.getSchemeSpecificPart() = " + uri.getSchemeSpecificPart());

		Log.d(tag, "uri.getHost() = " + uri.getHost());
		Log.d(tag, "uri.getPort() = " + uri.getPort());
		Log.d(tag, "uri.getPath() = " + uri.getPath());
		Log.d(tag, "uri.getLastPathSegment() = " + uri.getLastPathSegment());

		Log.d(tag, "uri.getQuery() = " + uri.getQuery());
		Log.d(tag, "uri.getQueryParameterNames() = " + uri.getQueryParameterNames());
		Set<String> keys = uri.getQueryParameterNames();
		Log.d(tag, "keys.size() = " + keys.size());
		for(String key : keys) {
			Log.d(tag, "key , value = " + key + " , " + uri.getQueryParameter(key));
		}

		Log.d(tag, "uri.getFragment() = " + uri.getFragment());

		Log.d(tag, "uri.getEncodedSchemeSpecificPart() = " + uri.getEncodedSchemeSpecificPart());
		Log.d(tag, "uri.getEncodedPath() = " + uri.getEncodedPath());
		Log.d(tag, "uri.getEncodedQuery() = " + uri.getEncodedQuery());
		Log.d(tag, "uri.getEncodedFragment() = " + uri.getEncodedFragment());
	}

	public static void debugBundle(String tag, Bundle bundle) {
		if(bundle == null) {
			Log.w(tag, "if(bundle == null)");
			return;
		}

		Set<String> keys = bundle.keySet();
		Log.d(tag, "bundle keys.size() = " + keys.size());

		for(String key : keys) {
			Object bundleObj = bundle.get(key);
			Log.d(tag, "key / value = " + key + " / " + bundleObj);

			if(bundleObj instanceof Bundle) {
				Bundle innerBundle = bundle.getBundle(key);
				debugBundle(tag, innerBundle);
			}
		}
	}

	public static void debugDeviceInfo() {
		Log.i(TAG, "debugDeviceInfo()");
		Log.d(TAG, "Build.BOARD = " + Build.BOARD);
		Log.d(TAG, "Build.BRAND = " + Build.BRAND);
		Log.d(TAG, "Build.CPU_ABI = " + Build.CPU_ABI);
		Log.d(TAG, "Build.DEVICE = " + Build.DEVICE);
		Log.d(TAG, "Build.DISPLAY = " + Build.DISPLAY);
		Log.d(TAG, "Build.FINGERPRINT = " + Build.FINGERPRINT);
		Log.d(TAG, "Build.HOST = " + Build.HOST);
		Log.d(TAG, "Build.ID = " + Build.ID);
		Log.d(TAG, "Build.MANUFACTURER = " + Build.MANUFACTURER);
		Log.d(TAG, "Build.MODEL = " + Build.MODEL);
		Log.d(TAG, "Build.PRODUCT = " + Build.PRODUCT);
		Log.d(TAG, "Build.TAGS = " + Build.TAGS);
		Log.d(TAG, "Build.TYPE = " + Build.TYPE);
		Log.d(TAG, "Build.USER = " + Build.USER);
		Log.d(TAG, "Build.VERSION.RELEASE = " + Build.VERSION.RELEASE);
	}

	public static void responseResult(String code, String msg, OnResultListener onResultListener) {
		Log.e(TAG, "onResultListener = " + onResultListener);
		if(onResultListener != null) {
			JSONObject result = new JSONObject();
			try {
				result.putOpt(Result.KEY_code, code);
				result.putOpt(Result.KEY_msg, msg);
				onResultListener.onResult(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 스토어 로그인 사용자(userid)와 매칭되는 token을 받아온다.
	 * @param context
	 * @param storeType 정직원(MEMBER) or 협력직원(PARTNER)
	 * @return
	 */
	public static LoginUserInfo getUserInfo(Context context, StoreType storeType) {
		Log.i(TAG, "getUserInfo(Context context, StoreType storeType)");
		Cursor loginUserInfoCursor = null;

		switch(storeType) {
			case MEMBRER:
				loginUserInfoCursor = context.getContentResolver().query(SKIMP_Store_M_Lib.URI_LOGIN_USER,
						null, null, null, null, null);
				break;
			case PARTNER:
				loginUserInfoCursor = context.getContentResolver().query(SKIMP_Store_P_Lib.URI_LOGIN_USER,
						null, null, null, null, null);
				break;
		}

		if (loginUserInfoCursor != null) {
			try {
				if (loginUserInfoCursor.moveToFirst()) {
					try {
						int id = loginUserInfoCursor.getInt(loginUserInfoCursor.getColumnIndexOrThrow(TBL_LOGIN_USER._ID));
						String encUserId = loginUserInfoCursor.getString(loginUserInfoCursor.getColumnIndexOrThrow(TBL_LOGIN_USER.COL_userId));
						Log.e(TAG, "id / encUserId = " + id + " / " + encUserId);
						String userid = getDecryptedUserId(context, storeType, encUserId);
						String token = loginUserInfoCursor.getString(loginUserInfoCursor.getColumnIndexOrThrow(TBL_LOGIN_USER.COL_token));
						String encPwd = loginUserInfoCursor.getString(loginUserInfoCursor.getColumnIndexOrThrow(TBL_LOGIN_USER.COL_encPwd));
						Log.e(TAG, "id / userid = " + id + " / " + userid);
						Log.e(TAG, "id / token = " + id + " / " + token);
						Log.e(TAG, "id / encPwd = " + id + " / " + encPwd);
						return new LoginUserInfo(userid, token, encPwd);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} finally {
				loginUserInfoCursor.close();
			}
		}

		return null;
	}

	/**
	 * 스토어 로그인 사용자(userid)와 매칭되는 token을 받아온다.
	 * @param context
	 * @param encUserId 정직원(MEMBER) or 협력직원(PARTNER)
	 * @return
	 */
	private static String getDecryptedUserId(Context context, StoreType storeType, String encUserId) {
		Log.i(TAG, "getDecryptedUserId(Context context, StoreType storeType, String encUserId)");
		Log.d(TAG, "storeType = " + storeType);
		Log.d(TAG, "encUserId = " + encUserId);
		Cursor keyInfoCursor = null;

		switch(storeType) {
			case MEMBRER:
				keyInfoCursor = context.getContentResolver().query(SKIMP_Store_M_Lib.URI_KEY,
						null, null, null, null, null);
				break;
			case PARTNER:
				keyInfoCursor = context.getContentResolver().query(SKIMP_Store_P_Lib.URI_KEY,
						null, null, null, null, null);
				break;
		}

		if (keyInfoCursor != null) {
			try {
				if (keyInfoCursor.moveToFirst()) {
					try {
						String key = keyInfoCursor.getString(keyInfoCursor.getColumnIndexOrThrow(TBL_KEY.COL_key));
						String decrytedUserId = AES256Util.decrypt(key, encUserId);
						Log.e(TAG, "decrytedUserId = " + decrytedUserId);
						return decrytedUserId;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} finally {
				keyInfoCursor.close();
			}
		}

		return null;
	}

}
