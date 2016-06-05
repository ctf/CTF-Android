package com.example.ctfdemo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true) 
public class Session {
	private String _id, _rev, role;
	public final String type = "session";
	private LdapUser user;
	Date expiration;
	private boolean persistent = true;
	public Session() {
	}
	public Session(String _id, LdapUser user, long expirationHours) {
		this._id = _id;
		this.user = user;
		this.expiration = new Date(System.currentTimeMillis() + (expirationHours * 60 * 60 * 1000));
	}
	public String toString() {
		return "Session " + this.getId();
	}
	@JsonProperty("_id")
	public String getId() {
		return _id;
	}
	@JsonProperty("_id")
	public void setId(String _id) {
		this._id = _id;
	}
	@JsonProperty("_rev")
	public String getRev() {
		return _rev;
	}
	@JsonProperty("_rev")
	public void setRev(String _rev) {
		this._rev = _rev;
	}
	public LdapUser getUser() {
		return user;
	}
	public void setUser(LdapUser user) {
		this.user = user;
	}
	public Date getExpiration() {
		return expiration;
	}
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	public boolean isPersistent() {
		return persistent;
	}
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

}