package ca.mcgill.science.ctf.tepid;

public class SessionRequest {

	public String username, password;

	public boolean persistent, permanent;

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

}