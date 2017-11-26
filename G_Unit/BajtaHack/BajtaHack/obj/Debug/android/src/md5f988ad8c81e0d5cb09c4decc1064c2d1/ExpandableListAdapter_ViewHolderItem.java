package md5f988ad8c81e0d5cb09c4decc1064c2d1;


public class ExpandableListAdapter_ViewHolderItem
	extends java.lang.Object
	implements
		mono.android.IGCUserPeer
{
/** @hide */
	public static final String __md_methods;
	static {
		__md_methods = 
			"";
		mono.android.Runtime.register ("BajtaHack.ExpandableListAdapter+ViewHolderItem, BajtaHack, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", ExpandableListAdapter_ViewHolderItem.class, __md_methods);
	}


	public ExpandableListAdapter_ViewHolderItem ()
	{
		super ();
		if (getClass () == ExpandableListAdapter_ViewHolderItem.class)
			mono.android.TypeManager.Activate ("BajtaHack.ExpandableListAdapter+ViewHolderItem, BajtaHack, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", "", this, new java.lang.Object[] {  });
	}

	private java.util.ArrayList refList;
	public void monodroidAddReference (java.lang.Object obj)
	{
		if (refList == null)
			refList = new java.util.ArrayList ();
		refList.add (obj);
	}

	public void monodroidClearReferences ()
	{
		if (refList != null)
			refList.clear ();
	}
}
