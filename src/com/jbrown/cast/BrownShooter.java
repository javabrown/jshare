package com.jbrown.cast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class BrownShooter {
	private String _cookies;
	private HttpClient _client;
	private final String USER_AGENT = "Mozilla/5.0";

	public BrownShooter() {
		_client = new DefaultHttpClient();
		_cookies = null;
	}

	public String get(String url) throws Exception {
		HttpGet request = new HttpGet(url);

		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language", "en-US,en;q=0.5");

		HttpResponse response = _client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		// set cookies
		setCookies(response.getFirstHeader("Set-Cookie") == null ? ""
				: response.getFirstHeader("Set-Cookie").toString());

		return result.toString();
	}

	public String postImage(String url, String imageBase64, String ip) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("imageData", imageBase64));
		nameValuePairs.add(new BasicNameValuePair("ip", ip));

		try {
			return this.post(url, nameValuePairs);
		} catch (Exception ex) {
			System.err.println("Post failed!!");
			ex.printStackTrace();
		}

		return null;
	}

	public String post(String url, List<NameValuePair> postParams)
			throws Exception {
		HttpPost post = new HttpPost(url);

		// add header
		// post.setHeader("Host", "accounts.google.com");
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language", "en-US,en;q=0.5");
		post.setHeader("Cookie", getCookies());
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer",
				"https://accounts.google.com/ServiceLoginAuth");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		if (postParams != null) {
			post.setEntity(new UrlEncodedFormEntity(postParams));
		}

		HttpResponse response = _client.execute(post);

		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'POST' request to URL : " + url);
		// System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}

	public List<NameValuePair> getFormParams0(String html, String username,
			String password) throws UnsupportedEncodingException {
		System.out.println("Extracting form's data...");
		return null;
	}

	public String getCookies() {
		return _cookies;
	}

	public void setCookies(String cookies) {
		_cookies = cookies;
	}
}