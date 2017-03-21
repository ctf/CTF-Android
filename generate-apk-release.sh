#!/usr/bin/env bash

#create a new directory that will contain our generated apk
mkdir $HOME/CTFA/
# copy generated apk from build folder to the folder just created
cp -R CTF/build/outputs/apk/CTF-Android.apk $HOME/CTFA/

# go to home and setup git
echo "Clone Git"
cd $HOME
git config --global user.email "me@allanwang.ca"
git config --global user.name "Allan Wang"
# clone the repository in the buildApk folder
git clone --quiet --branch=master  https://AllanWang:$GITHUB_API_KEY@github.com/CTFMcGill/CTFMcGill.github.io.git  master > /dev/null
# create version file
echo "Create Version File"
cd master
echo "CTFA $TRAVIS_BUILD_NUMBER" >> CTFA.txt

echo "Push Version File"
git remote rm origin
git remote add origin https://AllanWang:$GITHUB_API_KEY@github.com/CTFMcGill/CTFMcGill.github.io.git
git add -f .
git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed [skip ci]"
git push -fq origin master > /dev/null

echo "Create New Release"
cd $HOME
API_JSON=$(printf '{"tag_name": "%s","target_commitish": "master","name": "%s","body": "Android Release %s","draft": false,"prerelease": false}' $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER)
newRelease=$(curl --data "$API_JSON" https://api.github.com/repos/CTFMcGill/CTFMcGill.github.io/releases?access_token=$GITHUB_API_KEY)
rID=`echo $newRelease | jq ".id"`
curl -H "Content-Type:application/zip" \
-X POST --data "data=@CTFA/CTF-Android.apk" https://api.github.com/repos/CTFMcGill/CTFMcGill.github.io/releases/$rID/assets?access_token=$GITHUB_API_KEY&name=CTFA-$TRAVIS_BUILD_NUMBER.apk

echo -e "Done\n"