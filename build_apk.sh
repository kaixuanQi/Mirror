#!/bin/bash
starttime=`date +%s`
_alpbet='abcdefghijklmnopqrstuvwxyz'
_numbers='0123456789'
_chars='abcdefghijklmnopqrstuvwxyz0123456789'

function getPkgName(){
    local cstr="com."
    for i in $( seq 1 4)
    do
     if [ $i -eq 1 ] || [ $i -eq 4 ]; then
       cstr=${cstr}${_alpbet:$(($RANDOM%26)):1}
    else
       cstr=${cstr}${_chars:$(($RANDOM%36)):1}
    fi
    done
    cstr=${cstr}"."
    for i in $( seq 1 3)
    do
     if [ $i -eq 1 ] || [ $i -eq 3 ]; then
       cstr=${cstr}${_alpbet:$(($RANDOM%26)):1}
    else
       cstr=${cstr}${_chars:$(($RANDOM%36)):1}
    fi
    done
    echo ${cstr}
}


mjb=$1
mjbpkg=$2
if [ ${mjb}temp == ""temp ]; then
    echo "!mjb"
    exit
fi

if [ ${mjbpkg}temp == ""temp ]; then
    echo "mjbpkgi null"
    exit
fi

mainChId=`grep "MainChId" AndroidManifest.xml | awk -F\" '{print $4}'`
subChId=`grep "SubChId" AndroidManifest.xml | awk -F\" '{print $4}'`
sed -i "s/mainChId=\".*\"/mainChId=\"${mainChId}\"/g"  config.gradle
sed -i "s/subChId=\".*\"/subChId=\"${subChId}\"/g"  config.gradle

echo $mainChId"_"$subChId"_"$mjbpkg

if [ ${mainChId} -eq 16622 ] || [ ${mainChId} -eq 17622 ] || [ ${mainChId} -eq 18622 ] || [ ${mainChId} -eq 20622 ]; then
    rm -rf platform/vm_new/${mjb}/*
    cp -rf platform/vm_new/vivo_new/${mjb}/* platform/vm_new/${mjb}/
fi
if [ ${mainChId} -eq 18022 ] && ([ ${subChId} -eq 8 ] || [ ${subChId} -eq 9 ] || [ ${subChId} -eq 52 ]) || ([ ${mainChId} -eq 16022 ] && [ ${subChId} -eq 52 ]); then
    cp -rf platform/vm_new/${mjb}/*.sh rename_vm.sh
    cp -rf platform/vm_new/${mjb}/*.jar platform/vm_new/
#    cp -rf platform/vm_new/${mjb}/*.jar platform/vm_new/arm64/
    cp -rf platform/vm_new/${mjb}/${subChId}/refactor.map ./
elif [ ${mainChId} -eq 25022 ] && ([ ${subChId} -eq 8 ] || [ ${subChId} -eq 25 ]); then
    cp -rf platform/vm_new/${mjb}/*.sh rename_vm.sh
    cp -rf platform/vm_new/${mjb}/*.jar platform/vm_new/
#    cp -rf platform/vm_new/${mjb}/*.jar platform/vm_new/arm64/
    cp -rf platform/vm_new/${mjb}/8/refactor.map ./
elif [ ${mjbpkg}temp = "com.zwang.clouds"temp ] && [ ${mainChId} -eq 25022 ] && ( [ ${subChId} -eq 24 ] || [ ${subChId} -eq 27 ] ); then
    cp -rf platform/vm_new/1002/*.sh rename_vm.sh
    cp -rf platform/vm_new/1002/*.jar platform/vm_new/
    cp -rf platform/vm_new/1002/refactor.map ./
else
    cp -rf platform/vm_new/${mjb}/*.sh rename_vm.sh
    cp -rf platform/vm_new/${mjb}/*.jar platform/vm_new/
#    cp -rf platform/vm_new/${mjb}/*.jar platform/vm_new/arm64/
    cp -rf platform/vm_new/${mjb}/refactor.map ./
fi
apkg=$mjbpkg
#if [ ${apkg}temp == ""temp ]; then
#    apkg=`grep "package=" AndroidManifest.xml | awk -F\" '{print $2}'`
#fi
#sed -i "s/pkg=\".*\"/pkg=\"${apkg}\"/g"  config.gradle
#sed -i "s/masterPkg=\".*\"/masterPkg=\"${apkg}\"/g"  config.gradle

vercode=`grep "versionCode=" config.gradle | awk -F\" '{print $2}'`
vername=`grep "versionName=" config.gradle | awk -F\" '{print $2}'`
mainch=`grep "mainChId=" config.gradle | awk -F\" '{print $2}'`
subch=`grep "subChId=" config.gradle | awk -F\" '{print $2}'`
#./listfile.sh export_jar/src $apkg

#iconpng=`grep "icon.png ->" refactor.map | awk -F"[ ]"  '{print $3}'`
#mainjar=`grep "main.jar ->" refactor.map | awk -F"[ ]"  '{print $3}'`

#if [ ${iconpng}temp != ""temp ]; then
#    sed -i "s/\<icon.png\>/$iconpng/g" export_jar/src/com/excelliance/kxqp/KXQPApplication.java
#    sed -i "s/\<icon.png\>/$iconpng/g" main_apk/build.gradle app/build.gradle
#fi

#if [ ${mainjar}temp != ""temp ]; then
#    sed -i "s/\<main.jar\>/$mainjar/g" export_jar/src/com/excelliance/kxqp/KXQPApplication.java
#fi
echo "finish rename"
endtime=$(date +%s)
echo "take $((endtime-starttime))s"
gradlew clean
gradlew -p app build

echo "finish build"

#outdir=$2
#if [ ${outdir}temp == ""temp ]; then
#    outdir="apk"
#fi
#
#if [ ! -d ${outdir} ]; then
#    mkdir -p ${outdir}
#fi
#
#cp -rf app/build/outputs/apk/${apkg}"_"${mainch}"_"${subch}"_release.apk" ${outdir}"/"${apkg}"_"${vercode}"_"${vername}"_release.apk"

echo "rename take $((endtime-starttime))s"
endtime2=$(date +%s)
echo "total take $((endtime2-starttime))s"
