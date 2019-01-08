/* Description : An XML Parser. Will parse an XML file and generate a tab delimited txt file of unique tags only or tags with their values.
 * 				txt file will have tags indented properly to keep their hierarchies. 
 * Purpose	   : quick way to remove all unwanted characters, Tag metadata, and values in order to inspect XML structure. 
 * Author	   : Rodrigo Sejas Jaldin
 * Usage       : javac parser.java [XMLfilepath] [-t|-v|-x|-u] [outputFilePath]    3 args are required. void the brackets 
 * 
 * 				-t for tags only.
 * 				-v for tags and values.
 * 				-u for unique tags only. Does not print repeating instances of tags.
 * 				-x for xpath of unique tags.
 * 	
 *  TO DO  1)  -u option prints out unique 1:M tags but does not take child fields into account.
 *  				  Meaning if another repeating parent Tag further in xml has more child filds, this tag
 *  				  will not be present in output file because this parent tag will be marked as duplicate.
 *  				  Fix : add futher hashmap comparisons for tags. 
 *  
 */

import java.io.*;
import java.util.*;

public class Parser {

	private File XMLfile = null; //xml file
	private ArrayList<TagNodes> allTags= new ArrayList<TagNodes>();       //list of all parent tags in xml
	private ArrayList<TagNodes> currentTags= new ArrayList<TagNodes>();   //list of current tags in xml


	public Parser(String XMLFile, String typeOfOutput, String outputFileName){
		//allTags = new ArrayList<TagNodes>();  
		//currentTags = new ArrayList<TagNodes>();
		XMLfile = new File(XMLFile);
		traverse(XMLfile);
		createCSVFile(typeOfOutput, outputFileName);

	}

	//prepares xml file and starts traversing file. 
	private void traverse(File XMLfile) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(XMLfile));
			String line;
			while((line=reader.readLine())!= null) {
				//System.out.println(line);
				typeOfTag(line);
			}
		} catch (IOException e) {e.printStackTrace();}
		finally {
			try {reader.close();} catch (IOException e) {e.printStackTrace();}
		}
	}

	/* purpose of this method is to determine whether current line is the start of a 
	 * repeating group (table), a field tag, or a table closing tag.
	 */
	private void typeOfTag(String xmlLineText){
		Boolean isNewTableTag = xmlLineText.contains("/");
		String[] lineValues = xmlLineText.split("(</)|(>)|(<)"); //splits strings at the following chars </ , < , > .
		if (lineValues.length>=2) {
			String[] removeMetadata = lineValues[1].split(" "); //remove tag's metadata if applicable
			lineValues[1]=removeMetadata[0];
		}
		if (isNewTableTag != true) newTableTag(xmlLineText);     //table tags will not have a closing tag in same line.
		else if(lineValues.length>3) fieldTag(lineValues);       //must be > 3 - array with field value ["","tagName","value","closingTagname",""]
		else
			closingTableTag(lineValues);                             

	}
	//handles new (table) tags.
	private void newTableTag(String text) {
		String[] lineValues = text.split("(</)|(>)|(<)"); // will create an array of 2 items. [0] blank, [1] tag text 
		if (lineValues.length>=2) {
			String[] removeMetadata = lineValues[1].split(" ");
			lineValues[1]=removeMetadata[0];
		}
		TagNodes newTag;
		if(currentTags.isEmpty()==true) 
			newTag = new TagNodes(lineValues[1]);
		else {
			TagNodes tempParentNode = currentTags.get(currentTags.size()-1);
			newTag = new TagNodes(lineValues[1],tempParentNode);
		}
		allTags.add(newTag);
		currentTags.add(newTag);
	}

	//new fields for current table tag. line array has 4 indexes from
	//previous method example : [,fieldName,value,fieldname]. only care about field name and value - index 1,2. 
	private void fieldTag(String[] text) {
		TagNodes tempTag = currentTags.get(currentTags.size()-1);
		tempTag.addFieldValue(text[1], text[2]);

	}
	//closing tag for current table tag.
	private void closingTableTag(String[] text) {
		TagNodes tempTag = currentTags.get(currentTags.size()-1);
		String currentTagName = tempTag.getTagName();
		if(currentTagName.equals(text[1])) currentTags.remove(tempTag);
	}

	public String toString(String typeOfOutput){
		String tags = "";
		if(typeOfOutput.equals("-u")==true){          //-u option will only print out unique tags 
			ArrayList<TagNodes> uniqueTags=this.isNextTagSameTag();
			for(int i=0;i<uniqueTags.size();i++) {
				TagNodes temptag = uniqueTags.get(i);
				tags+=temptag.toStringTags();
			}
		}
		else if(typeOfOutput.equals("-x")==true){          //-x option will print out xPath of unique Tags
			ArrayList<TagNodes> uniqueTags=this.isNextTagSameTag();
			for(int i=0;i<uniqueTags.size();i++) {
				TagNodes temptag = uniqueTags.get(i);
				tags+=temptag.toStringXPath();
			}
		}
		else{
			for(int i=0; i<allTags.size();i++) {   //this will print out full tags and child fields
				TagNodes temptag = allTags.get(i);
				if(typeOfOutput.equals("-t"))      //-t option if user only wants tag in output file
					tags+=temptag.toStringTags();
				else									//-v or no option is given, tag and values will be printed.
					tags+=temptag.toStringFields();
			}
		}
		return tags;
	}

	//This method compares all tags that were read and creates a new ArrayList of only unique tags.
	//
	//TO DO - further HashMap comparison can be made in order to also compare child fields and values. For now
	//only use to visualize xml structure hierarchy and parent tags with 1:M relationship.
	public ArrayList<TagNodes> isNextTagSameTag() {    
		ArrayList<TagNodes> uniqueTags= new ArrayList<TagNodes>();
		uniqueTags.add(allTags.get(0));
		for(int i=1;i<allTags.size();i++){
			TagNodes currentTag=allTags.get(i);
			boolean duplicateFound=false;
			for(int j=0;j<uniqueTags.size();j++) {
				TagNodes tempTag=uniqueTags.get(j);
				//System.out.println("all"+currentTag.getXPath());
				//System.out.println("unique"+tempTag.getXPath());
				if(currentTag.getXPath().equals(tempTag.getXPath())==true){
					duplicateFound=true;
					break;
				}
			}
			if(duplicateFound==false)
			uniqueTags.add(allTags.get(i));
		}
		//for(int i=1; i<uniqueTags.size();i++){
			//System.out.println(uniqueTags.get(i).getXPath());
		//}
		return uniqueTags;
	}

	public void createCSVFile(String typeOfOutput, String outputFileName)
	{
		try {
			// create Tab delimited file 
			FileWriter outPut = new FileWriter(outputFileName);
			PrintWriter outPutFile = new PrintWriter(outPut);
			String outPutText = toString(typeOfOutput);
			outPutFile.println(outPutText);
			// loop through all your data and print it to the file
			outPutFile.close();
		} catch (IOException e) {
			System.out.println("Error while creating CSV output file");
		}
	}

	public static void main(String[] args) {
		
		//assumes use of wrapper shell script to pre-validate xml files and arguments.
		new Parser(args[0], args[1], args[2]);

		//TO DO - implement safety checks when not used by wrapper script.
		
	}

}
