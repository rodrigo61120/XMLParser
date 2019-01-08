#!/bin/bash
################################################
# Description : wrapper script for JAVA XML parser
#				JAVA Parser will generate output of tags only or tags and values. will remove all special characters and metadata
# Author 	  : Rodrigo Sejas Jaldin
# Usage		  : sh XMLParserWrapper.sh -s XMLFilePath [-t|-v|-x|-u] -d outputTargerFilePath   
#				sh XMLParserWrapper.sh [-t|-v|-x|-u] 			-assumes source path and destination path are hard coded into shell script   
#				
#				-t for tags only.
#				-v for tags and values.
#				-u for unique tags only. Does not print repeating instances of tags.
#				-x for xpath of unique tags.
#				
# Notes       :	can handle multiple xml files. will generate 1 output file for each.
#
# To do		  : add log file generator to capture entire process. Implement safety checks throughout to capture run time errors.		
#################################################

#hard code the source and destination paths if always using the same source and destination path. 
#Dont forget closing / EX: source_path=/Users/..../xmlSource/
source_path=
destination_path=

jar_file_path=$(pwd)/           #jar file location. change if needed. dont forget closing /
date_stamp=$(date +%Y-%m-%d)

tag_option=none
tag_option_value=
num=0							#safety check to make sure only 1 tag_option is given as parameter. 

#while getopts "s:tvuxd:" parm
while getopts "stvuxd" parm
do	case	$parm in
	(s)		source_path="$OPTARG";;
	(t)		tag_option="-t"
			num=$((num+1))									
			tag_option_value="Tags only";;           
	(v)		tag_option="-v"
			num=$((num+1))
			tag_option_value="tags + values";;
	(x)		tag_option="-x"
			num=$((num+1))
			tag_option_value="xPath of Tags";;
	(u)		tag_option="-u"
			num=$((num+1))
			tag_option_value="Unique Tags only (no repeating tags)";;
	(d)		destination_path="$OPTARG";;
	(?) 	printf 'Usage:\n
1) sh XMLParserWrapper.sh -s source_path [-t|-v|-x|-u] -d destination_path \n
2) sh XMLParserWrapper.sh [-t|-v|-x|-u] 	 \n
\n
	[-t] for tags only. \n
	[-v] for tags and values. \n
	[-u] for unique tags only. Does not print repeating instances of tags. \n
	[-x] for xpath of unique tags. \n
\n
Exiting now...\n\n';
			exit 1;;
	esac
done
shift $(($OPTIND - 1))

printf '\nCHECKING required variables... \n\n';

#parameter safety checks
if [ -z $source_path ]
	then echo -e "\nERROR\nNo XML file Source Path found. Either use [-s] parameter or hard code path to script \n"
	exit 1
elif [ -z $destination_path ]
	then echo -e "\nERROR\nNo Destination Path for output file found. Either use [-d] parameter or hard code path to script\n"
	exit 1
elif [ $num -ne 1 ];
	then echo -e "\nERROR\nRequires only 1 valid tag option parameter. Please use -t to get Tags only, -v for tags + Values, -x for tag's xPath, or -u for Unique tags only (no repeating tags)\n"
	exit 1
fi

#variable values.
echo
echo 'Jar_file_path=' $jar_file_path
echo 'source_path=' $source_path
echo 'destination_path=' $destination_path
echo 'tag_option_parameter=' $tag_option [$tag_option_value]
echo

num_of_files=0
for i in ${source_path}/*.xml; do
    num_of_files=$((num_of_files+1))
done

files_counter=0

printf 'STARTING... \n\n';


for currentFile in ${source_path}/*.xml; 
do	
		currentFileName=$(basename $currentFile) 		#gets file name and uses same name for output csv file.
		outputFileName=$(basename $currentFile .xml)
		files_counter=$((files_counter+1))
		echo 'progress: '$files_counter 'of '$num_of_files files'\n'
		echo 'Working on File:' $currentFileName
		echo java -jar ${jar_file_path}XMLParser.jar ${source_path}${currentFileName} $tag_option ${destination_path}${outputFileName}_${date_stamp}.txt
		java -jar ${jar_file_path}XMLParser.jar ${source_path}${currentFileName} $tag_option ${destination_path}${outputFileName}_${date_stamp}.txt
        echo 'Done: file has been generated for' $currentFileName
        echo
done

printf 'FINISHED - XML Parser complete. Check output folder for files.\n\n'



