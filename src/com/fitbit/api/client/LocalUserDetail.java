package com.fitbit.api.client;

public class LocalUserDetail {
	private final String loginID;
	private final String userId;
	
	public LocalUserDetail(String loginID,String userId) {
		this.userId = userId;
		this.loginID=loginID;
	}
	
	public String getUserId() {
		return userId;
	}

	public String getLoginID() {
		return loginID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (userId == null ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		LocalUserDetail other = (LocalUserDetail) obj;
		if (userId == null) {
			if (other.userId != null) return false;
		} else if (!userId.equals(other.userId)) return false;
		return true;
	}

}
