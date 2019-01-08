import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.io.*;

public class TagNodes {

	private String tagName; 							   //name of tag
	private HashMap<String,ArrayList<String>> hashMap; // field and values of all tags. Hashmap will handle 1:M tags
	private TagNodes parentTag;							//Parent tag
	private String xPath=""; 
	public int depth;
	
	public TagNodes(String tagText, TagNodes parentTag){
		tagName=tagText;;
		hashMap = new HashMap<String, ArrayList<String>>();
		//if (parentTag==null) {   //may not need this, handled by 2nd constructor.

		this.parentTag=parentTag;
		depth=parentTag.getDepth()+1;
		xPath+=parentTag.getXPath()+"/"+tagText;
		
	}
	public TagNodes(String tagText){
		tagName=tagText;
		hashMap = new HashMap<String, ArrayList<String>>();
		parentTag=null;
		depth=0;
		xPath=tagText;
	}

	public void addFieldValue(String field, String value) {
		//add value to existing field 
		boolean existingField = hashMap.containsKey(field);
		ArrayList<String> fieldValues;
		if (existingField==true) {
			fieldValues = hashMap.get(field);
			fieldValues.add(value);
			hashMap.replace(field,fieldValues);										
		}
		//add new field & value 
		else {	
			fieldValues = new ArrayList<String>();
			fieldValues.add(value);
			hashMap.put(field, fieldValues);
		}
	}
	public int getDepth() {
		return depth;
	}

	public String getTagName(){
		return tagName;
	}
	public void setXPath(String xpath) {
		xPath=xpath;
	}
	public String getXPath() {
		return xPath;
	}
	//returns formatted text of all Tags and Values
	public String toStringFields() {
		String tags="";

		String numTabs = "";
		for(int i=0;i<depth;i++) {  
			numTabs+="\t";
		}
		if(parentTag!=null)
			//tags+=numTabs+ ""+this.parentTag.getTagName()+">>"+ tagName +"\n";
			tags+=numTabs+ ""+ tagName +"\n";
		else
			tags+=tagName +"\n";
		//Iterator iterate = hashMap.entrySet().iterator();
		Iterator<Entry<String, ArrayList<String>>> iterate = hashMap.entrySet().iterator();
		while (iterate.hasNext()) {
			Map.Entry<String, ArrayList<String>> pair = (Map.Entry<String, ArrayList<String>>)iterate.next();
			String currentKey = pair.getKey();
			ArrayList<String> currentValues = hashMap.get(currentKey);
			for(int i=0;i<currentValues.size();i++) {
				tags+=numTabs+ "\t"+currentKey+" : "+currentValues.get(i)+"\n";
			}
		}
		return tags;
	}
	//returns formatted text of all tags only
	public String toStringTags() {
		String tags="";

		String numTabs = "";      //depending on the depth of tag, it will increment corresponding tags for csv file.
		for(int i=0;i<depth;i++) {  
			numTabs+="\t";
		}
		if(parentTag!=null)
			//tags+=numTabs+ ""+this.parentTag.getTagName()+">>"+ tagName +"\n";
			tags+=numTabs+ ""+ tagName +"\n";
		else
			tags+=tagName +"\n";
		Iterator<Entry<String, ArrayList<String>>> iterate = hashMap.entrySet().iterator();
		while (iterate.hasNext()) {
			Map.Entry<String, ArrayList<String>> pair = (Map.Entry<String, ArrayList<String>>)iterate.next();
			String currentKey = pair.getKey();
			tags+=numTabs+ "\t"+currentKey+"\n";
		}
		return tags;
	}
	
	//will print xpaths for tags
	public String toStringXPath() {
		String tags="";

		String numTabs = "";      //depending on the depth of tag, it will increment corresponding tags for csv file.
		for(int i=0;i<depth;i++) {  
			numTabs+="\t";
		}
		if(parentTag!=null)
			// tags+=numTabs+ this.getXPath()+"/"+ tagName +"\n";
			tags+=numTabs+ xPath+"/"+"\n";
		else
			tags+=tagName +"\n";	
		
		Iterator<Entry<String, ArrayList<String>>> iterate = hashMap.entrySet().iterator();
		while (iterate.hasNext()) {
			Map.Entry<String, ArrayList<String>> pair = (Map.Entry<String, ArrayList<String>>)iterate.next();
			String currentKey = pair.getKey();
			tags+=numTabs+ "\t"+	this.getXPath()+"/"+currentKey+"\n";
		}
		return tags;
	}

	//~~~To be implemented - print out table format.~~~~~~~~~~~~~~~~~~
	/*
	public String tableFormatString() {
		String tags = "";
		tags+= "Table Name : "+ tagName;

		Iterator<Entry<String, ArrayList<String>>> iterate = hashMap.entrySet().iterator();
		ArrayList<String> allFields = new ArrayList<String>();
		int numRows = 0;
		while (iterate.hasNext()) {
			Map.Entry<String, ArrayList<String>> pair = (Map.Entry<String, ArrayList<String>>)iterate.next();
			String currentKey = pair.getKey();
			allFields.add(currentKey);
			ArrayList<String> currentValues = hashMap.get(currentKey);
			if(currentValues.size()>=numRows) numRows = currentValues.size();
		}
		String[][] tableValues = new String[allFields.size()][numRows+2];
		for(int i=0;i<allFields.size();i++) {		
		}
		return "";
	}
	*/
}
