package skimp.store.lib.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.sktelecom.ssm.lib.SSMLib;
import com.sktelecom.ssm.lib.SSMLibListener;
import com.sktelecom.ssm.lib.constants.SSMProtocolParam;
import com.sktelecom.ssm.remoteprotocols.ResultCode;

import org.json.JSONObject;

/**
 * MDM(SSM)
 */

public class MDM implements SSMLibListener {
	private static final String TAG = MDM.class.getSimpleName();

	private static final String MDM_DOWNLOAD_URL = "https://ssm-skimp.skinnovation.com:52444/inhouse";
	private static final String MDM_SERVER_URL = "https://ssm-skimp.skinnovation.com:52444";

	public static final int OK = SSMLib.OK;

	private static MDM sInstance;
	public static synchronized MDM getInstance() {
		if (sInstance == null) {
			sInstance = new MDM();
		}
		return sInstance;
	}

	/** MDM(SSM) 라이브러리 */
	private SSMLib ssmLib;

	private MDM() {
	}

	// 1. MDM(SSM) 설치확인 확인
	public boolean isInstalled() {
		return ssmLib.isInstalledSSM();
	}

	// 2. MDM(SSM) 설치페이지 이동
	public void goInstallPage(Context context) {
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MDM_DOWNLOAD_URL)));
	}

	// 3. MDM(SSM) 로그인
	public int login() {
		return ssmLib.setLoginStatus(SSMProtocolParam.LOGIN);
	}

	// 4. MDM(SSM) 로그아웃
	public int logout() {
		return ssmLib.setLoginStatus(SSMProtocolParam.LOGOUT);
	}

	public int init(Context context) {
		Log.i(TAG, "init(Context context)");

		/* SSMLib의 객체 생성 및 Listener를 등록 */
		ssmLib = SSMLib.getInstance(context);
		ssmLib.registerSSMListener(this);

		/* SSMLib 초기화 */
		int code = ssmLib.initialize();
		Log.e(TAG, "ssmLib.initialize() code = " + code);

		return code;
	}

	public void release() {
		ssmLib.release();
	}

	public int checkMDM(Context context) {
		Log.i(TAG, "checkMDM(Context context)");
		if(ssmLib == null) {
			init(context);
		}
		/* SSM 유효성 확인 */
		int result = ssmLib.checkSSMValidation();
		Log.e(TAG, "ssmLib.checkSSMValidation() result = " + result);

		// 단말에 설치된 MDM(SSM)이 예전 서버를 바라보고 있으면 새로운 MDM(SSM) 설치 유도를 위해 OLD_VERSION_INSTALLED 설정
		if(!ssmLib.getServerUrl().equals(MDM_SERVER_URL)) {
			result = ResultCode.OLD_VERSION_INSTALLED;
		}

		return result;
	}

	private void registerSSMListener(Context context) {
		Log.e(TAG, "registerSSMListener(Context context)");
		ssmLib.registerSSMListener(new SSMLibListener() {
			@Override
			public void onSSMInstalled() {
				Log.e(TAG, "onSSMInstalled()");
				/* SSM 설치 완료 되었을 경우 호출 됨 */

				/* SSMLib 초기화 */
				ssmLib.initialize();

				// SSAID는 keystore파일에 따라 달라짐. 따라서 여기서 ssaid 관련작업하면 안됨. 그래서 주석처리
//				// SSM앱과 스토어앱 ssaid 싱크
//				ssmLib.setSsmSsaid(getMySsaid(context));
			}

			@Override
			public void onSSMConnected() {
				Log.e(TAG, "onSSMConnected()");
				/* SSMLib 와 SSM가 binding 되었을 때 호출 됨 */

				/* SSM 유효성 확인 */
				int result = ssmLib.checkSSMValidation();
				Log.e(TAG, "ssmLib.checkSSMValidation() result = " + result);
			}

			@Override
			public void onSSMRemoved() {
				Log.e(TAG, "onSSMRemoved()");
			}

			@Override
			public void onSSMResult(String key, Object returnValue) {
				Log.e(TAG, "onSSMResult(String key, Object returnValue)");
				Log.e(TAG, "key / returnValue = " + key + " / " + returnValue);
			}
		});
	}

	public String getMessage(int result) {
		String str = "";
		switch (result) {
			case ResultCode.OK_PANDING:
				str = "타엡에서 SSM 제어를 이미 사용하고 있습니다.";
				break;
			case ResultCode.OK:
				str = "성공";
				break;
			case ResultCode.ERROR_CONNECTION:
				str = "바인딩 실패";
				break;
			case ResultCode.FAILED:
				str = "실패";
				break;
			case ResultCode.ERROR:
				str = "에러";
				break;
			case ResultCode.NOT_INSTALLED:
				str = "SSM 미설치";
				break;
			case ResultCode.UNREGISTERED:
				str = "SSM 미인증";
				break;
			case ResultCode.OLD_VERSION_INSTALLED:
				str = "SSM 이전버전 설치됨";
				break;
			case ResultCode.NO_PERMISSION:
				str = "앱이 권한을 가지고 있지 않습니다.";
				break;
		}

		return str;
	}


	@Override
	public void onSSMInstalled() {
		Log.e(TAG, "onSSMInstalled()");
		/* SSM 설치 완료 되었을 경우 호출 됨 */

		/* SSMLib 초기화 */
		ssmLib.initialize();
	}

	@Override
	public void onSSMConnected() {
		Log.e(TAG, "onSSMConnected()");
		/* SSMLib 와 SSM가 binding 되었을 때 호출 됨 */

		/* SSM 유효성 확인 */
		int result = ssmLib.checkSSMValidation();
		Log.e(TAG, "ssmLib.checkSSMValidation() result = " + result);
	}

	@Override
	public void onSSMRemoved() {
		Log.e(TAG, "onSSMRemoved()");
	}

	@Override
	public void onSSMResult(String key, Object returnValue) {
		Log.e(TAG, "onSSMResult(String key, Object returnValue)");
		Log.e(TAG, "key / returnValue = " + key + " / " + returnValue);
	}
}
