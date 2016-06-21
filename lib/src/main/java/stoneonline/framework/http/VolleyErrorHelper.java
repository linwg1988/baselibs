package stoneonline.framework.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.Map;

import www.stoneonline.com.lib.R;

public class VolleyErrorHelper {

	public static Context context;

	public static String getMessage(Object error) {
		if (error instanceof TimeoutError) {
			return context.getResources().getString(R.string.connect_server_error);
		} else if (isServerProblem(error)) {
			String string = handleServerError(error);
			return string;
		} else if (isNetworkProblem(error)) {
			return context.getResources().getString(R.string.network_problem);
		}
		return context.getResources().getString(R.string.network_error_try_again);
	}

	private static boolean isNetworkProblem(Object error) {
		return (error instanceof NetworkError)
				|| (error instanceof NoConnectionError);
	}

	private static boolean isServerProblem(Object error) {
		return (error instanceof ServerError)
				|| (error instanceof AuthFailureError);
	}

	public static int getResponseCode(VolleyError err) {
		NetworkResponse response = err.networkResponse;
		return response == null ? -1 : response.statusCode;
	}

	private static String handleServerError(Object err) {
		VolleyError error = (VolleyError) err;

		NetworkResponse response = error.networkResponse;

		if (response != null) {
			switch (response.statusCode) {
			case 404:
			case 422:
			case 401:
				try {
					// server might return error like this { "error":
					// "Some error occured" }
					// Use "Gson" to parse the result
					HashMap<String, String> result = new Gson().fromJson(
							new String(response.data),
							new TypeToken<Map<String, String>>() {
							}.getType());

					if (result != null && result.containsKey("error")) {
						String string = result.get("error");
						if(string == null){
							string = context.getResources().getString(R.string.network_error_try_again);
						}
						return string;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				// invalid request
				String msg = error.getMessage();
				if(msg == null){
					msg = context.getResources().getString(R.string.network_error_try_again);
				}
				return msg;

			default:
				return context.getResources().getString(R.string.connect_server_error);
			}
		}
		return context.getResources().getString(R.string.network_error_try_again);
	}
}