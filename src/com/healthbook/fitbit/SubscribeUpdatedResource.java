package com.healthbook.fitbit;

import java.util.List;

public class SubscribeUpdatedResource {
	class Update {
		String collectionType;
		String date;
		String ownerId;
		String ownerType;
		String subscriptionId;
	}

	public List<Update> updateList;

}
