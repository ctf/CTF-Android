package com.example.ctfdemo.tepid;

import com.example.ctfdemo.requests.DateJsonAdapter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LdapUser {
	public String displayName, givenName, middleName, lastName, shortUser, longUser, email, faculty, permaCode, nick, realName, salutation;
	public List<String> groups, preferredName;
	@JsonAdapter(DateJsonAdapter.class)
	public Date activeSince;
	public int studentId;

	private String _id, _rev;
	@JsonProperty("type")
	public final String type = "user";
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
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

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}
	
	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	@Override
	public String toString() {
		return "LdapUser [displayName=" + displayName + ", givenName=" + givenName + ", middleName=" + middleName
				+ ", lastName=" + lastName + ", shortUser=" + shortUser + ", longUser=" + longUser + ", email=" + email
				+ ", faculty=" + faculty + ", permaCode=" + permaCode + ", nick=" + nick + ", realName=" + realName
				+ ", salutation=" + salutation + ", groups=" + groups + ", preferredName=" + preferredName
				+ ", activeSince=" + activeSince + ", studentId=" + studentId + ", _id=" + _id + ", _rev=" + _rev
				+ ", type=" + type + ", additionalProperties=" + additionalProperties + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + ((activeSince == null) ? 0 : activeSince.hashCode());
		result = prime * result + ((additionalProperties == null) ? 0 : additionalProperties.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((faculty == null) ? 0 : faculty.hashCode());
		result = prime * result + ((givenName == null) ? 0 : givenName.hashCode());
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((longUser == null) ? 0 : longUser.hashCode());
		result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
		result = prime * result + ((nick == null) ? 0 : nick.hashCode());
		result = prime * result + ((permaCode == null) ? 0 : permaCode.hashCode());
		result = prime * result + ((preferredName == null) ? 0 : preferredName.hashCode());
		result = prime * result + ((realName == null) ? 0 : realName.hashCode());
		result = prime * result + ((salutation == null) ? 0 : salutation.hashCode());
		result = prime * result + ((shortUser == null) ? 0 : shortUser.hashCode());
		result = prime * result + studentId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LdapUser other = (LdapUser) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (activeSince == null) {
			if (other.activeSince != null)
				return false;
		} else if (!activeSince.equals(other.activeSince))
			return false;
		if (additionalProperties == null) {
			if (other.additionalProperties != null)
				return false;
		} else if (!additionalProperties.equals(other.additionalProperties))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (faculty == null) {
			if (other.faculty != null)
				return false;
		} else if (!faculty.equals(other.faculty))
			return false;
		if (givenName == null) {
			if (other.givenName != null)
				return false;
		} else if (!givenName.equals(other.givenName))
			return false;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (longUser == null) {
			if (other.longUser != null)
				return false;
		} else if (!longUser.equals(other.longUser))
			return false;
		if (middleName == null) {
			if (other.middleName != null)
				return false;
		} else if (!middleName.equals(other.middleName))
			return false;
		if (nick == null) {
			if (other.nick != null)
				return false;
		} else if (!nick.equals(other.nick))
			return false;
		if (permaCode == null) {
			if (other.permaCode != null)
				return false;
		} else if (!permaCode.equals(other.permaCode))
			return false;
		if (preferredName == null) {
			if (other.preferredName != null)
				return false;
		} else if (!preferredName.equals(other.preferredName))
			return false;
		if (realName == null) {
			if (other.realName != null)
				return false;
		} else if (!realName.equals(other.realName))
			return false;
		if (salutation == null) {
			if (other.salutation != null)
				return false;
		} else if (!salutation.equals(other.salutation))
			return false;
		if (shortUser == null) {
			if (other.shortUser != null)
				return false;
		} else if (!shortUser.equals(other.shortUser))
			return false;
		if (studentId != other.studentId)
			return false;
		return true;
	}
	
}
