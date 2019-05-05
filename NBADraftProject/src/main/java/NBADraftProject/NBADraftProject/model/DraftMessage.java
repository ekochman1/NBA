package NBADraftProject.NBADraftProject.model;

public class DraftMessage {
	private MessageType type;
	private String pick;
	private String sender;
	private String name;

	public enum MessageType {
		CHAT,
		DRAFT,
		JOIN,
		LEAVE,
		DONE
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getPick() {
		return pick;
	}

	public void setPick(String pick) {
		this.pick = pick;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getName() { return this.name; }

	public void setName(String name) { this.name = name; }
}

