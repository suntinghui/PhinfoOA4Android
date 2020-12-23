package com.heqifuhou.chrome;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;

public class MyWebChromeClient extends WebChromeClient{
	private INetWebChromeClient mInetWeb;
	public MyWebChromeClient(INetWebChromeClient inetWeb)
	{
		this.mInetWeb = inetWeb;
	}
	public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		Builder builder = new Builder(view.getContext());
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		});
		builder.setCancelable(false);
		builder.create();
		builder.show();
		return true;
	}

	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		Builder builder = new Builder(view.getContext());
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		});
		builder.setNeutralButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.cancel();
			}
		});
		builder.setCancelable(false);
		builder.create();
		builder.show();
		return true;
	}

	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
		LayoutInflater inflater = LayoutInflater.from(view.getContext());
		final View v = inflater.inflate(R.layout.act_chrome_edit, null);
		((TextView) v.findViewById(R.id.TextView_PROM)).setText(message);
		((EditText) v.findViewById(R.id.EditText_PROM)).setText(defaultValue);
		Builder builder = new Builder(view.getContext());
		builder.setView(v);
		builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String value = ((EditText) v.findViewById(R.id.EditText_PROM)).getText().toString();
				result.confirm(value);
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.cancel();
			}

		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				result.cancel();
			}
		});
		builder.create();
		builder.show();
		return true;
	}

	public void onProgressChanged(WebView view, int newProgress) {
		if(mInetWeb!=null){
			mInetWeb.onProgressChanged(view, newProgress);
		}
	}

	public void onReceivedTitle(WebView view, String title) {
		if(mInetWeb!=null){
			mInetWeb.onReceivedTitle(view, title);
		}
	}
}
