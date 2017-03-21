#!/usr/bin/env bash
# https://medium.com/@daggerdwivedi/push-your-apk-to-your-github-repository-from-travis-11e397ec430d#.5v6g5lpxx
# https://github.com/Glucosio/glucosio-android/blob/develop/upload-gh-pages.sh

#create a new directory that will contain our generated apk
# mkdir $HOME/CTFA/
#copy generated apk from build folder to the folder just created
cp -R CTF/build/outputs/apk/CTF-Android.apk $HOME/CTFA/
#go to home and setup git
cd $HOME
# git config --global user.email "me@allanwang.ca"
# git config --global user.name "Allan Wang"
#clone the repository in the buildApk folder
# git clone --quiet --branch=master  https://AllanWang:83d7c29abda5b326b375e4192def9a9aca112d02@github.com/CTFMcGill/CTFMcGill.github.io.git  master > /dev/null
#go into directory and copy data we're interested
# cd master
# cp -Rf $HOME/CTFA/* .
echo "Create release"
TRAVIS_BUILD_NUMBER=0
API_JSON=$(printf '{"tag_name": "%s","target_commitish": "master","name": "%s","body": "Android Release %s","draft": false,"prerelease": false}' $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER)
newRelease=$(curl --data '{"tag_name": "1","target_commitish": "master","name": "1","body": "Android Release 0","draft": false,"prerelease": false}' https://api.github.com/repos/CTFMcGill/CTFMcGill.github.io/releases?access_token=83d7c29abda5b326b375e4192def9a9aca112d02)
echo "hi"
echo "$newRelease"
echo $newRelease | jq ".id"
echo "Upload binary"
curl -i \
-H "Accept: application/json" \
-H "Content-Type:application/zip" \
-X POST --data "data=@$HOME/CTFA/*" (printf 'https://api.github.com/repos/CTFMcGill/CTFMcGill.github.io/releases/%s/assets?access_token=83d7c29abda5b326b375e4192def9a9aca112d02&name=CTFA-%s.apk' $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_NUMBER)

#add, commit and push files
git remote rm origin
git remote add origin https://AllanWang:83d7c29abda5b326b375e4192def9a9aca112d02@github.com/CTFMcGill/CTFMcGill.github.io.git
git add -f .
git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed [skip ci]"
git push -fq origin master > /dev/null
echo "Done\n"