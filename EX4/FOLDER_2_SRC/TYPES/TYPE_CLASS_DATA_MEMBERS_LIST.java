package TYPES;

public class TYPE_CLASS_DATA_MEMBERS_LIST {
	
	public TYPE_CLASS_DATA_MEMBER head;
	public TYPE_CLASS_DATA_MEMBERS_LIST tail;
	
	public TYPE_CLASS_DATA_MEMBERS_LIST(TYPE_CLASS_DATA_MEMBER head,TYPE_CLASS_DATA_MEMBERS_LIST tail)
	{
		this.head = head;
		this.tail = tail;
	}
}
