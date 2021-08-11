#!/bin/bash
starttime=`date +%s`
declare -A map=()
mjb=$1
echo "first mjb="$mjb
ppath=$2
rpath=""
echo "ppath="${ppath}
if [ ${ppath}temp != ""temp ]; then
    ppath=${ppath}"/"
    rpath=${ppath}
fi

while read line || [ -n "$line" ]
do
        orig=`echo $line|cut -d " " -f1`
        if [ ${orig}temp == ""temp ]; then
            continue;
        fi
        rname=`echo $line|cut -d " " -f3`
        rname=`echo ${rname%?}`
        sed -i "s/${orig}/${rname}/g" MirrorPlugin/module/src/main/AndroidManifest.xml
        sed -i "s/${orig}/${rname}/g" MirrorPlugin/special/src/main/AndroidManifest.xml
#        find main_jar/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
#        find account/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
##        find aes/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
##        find app/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
##        find arch-mvvm/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
##        find common_jar/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
#        find MirrorPlugin/special/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
#        find MirrorPlugin/special/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
##        find push_manufacturer/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
##        find statistics-middleware/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
##        find statisticsIndex/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
##        find tools/src/main/res/layout -type f -iregex ".*\.\(xml\)" | xargs sed -i "s/\<$orig\>/$rname/g"
#        find MirrorPlugin/special/src/main/java -type f -iregex ".*\.\(java\|aidl\)" | xargs sed -i "s/\"$orig\"/\"$rname\"/g"

done < ${rpath}refactor.map 

endtime=$(date +%s)
echo "rename main take $((endtime-starttime))s"
