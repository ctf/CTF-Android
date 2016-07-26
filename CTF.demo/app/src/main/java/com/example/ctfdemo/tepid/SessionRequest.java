package com.example.ctfdemo.tepid;

import com.google.api.client.http.HttpContent;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.io.OutputStream;

public class SessionRequest implements HttpContent {

	@Key
	public String username;

	@Key
	public String password;

	@Key
	public boolean persistent;

	@Key
	public boolean permanent;

	public SessionRequest withUsername(String username) {
		this.username = username;
		return this;
	}

	public SessionRequest withPassword(String password) {
		this.password = password;
		return this;
	}

	public SessionRequest withPersistent(boolean persistent) {
		this.persistent = persistent;
		return this;
	}

	public SessionRequest withPermanent(boolean permanent) {
		this.permanent = permanent;
		return this;
	}

	@Override
	public long getLength() throws IOException {
		return -1;
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public boolean retrySupported() {
		return true;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		byte[] sr = ("{username:" + this.username +
				", password:" + this.password +
				", persistent:" + this.persistent +
				", permanent:" + this.permanent + "}")
				.getBytes();
		out.write(sr);
		out.flush();
	}
}