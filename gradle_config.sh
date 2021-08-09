#!/bin/bash
app_package_name=$(grep "package=" AndroidManifest.xml | awk -F\" '{print $2}')
echo app_package_name=$app_package_name
sed -i "s/pkg = \".*\"/pkg = \"${app_package_name}\"/g" config.gradle

mainChId=$(grep "MainChId" AndroidManifest.xml | awk -F\" '{print $4}')
subChId=$(grep "SubChId" AndroidManifest.xml | awk -F\" '{print $4}')
sed -i "s/mainChId = \".*\"/mainChId = \"${mainChId}\"/g" config.gradle
sed -i "s/subChId = \".*\"/subChId = \"${subChId}\"/g" config.gradle

echo ${mainChId}_${subChId}
