#!/usr/bin/env bash
# https://medium.com/@daggerdwivedi/push-your-apk-to-your-github-repository-from-travis-11e397ec430d#.5v6g5lpxx
# https://github.com/Glucosio/glucosio-android/blob/develop/upload-gh-pages.sh

#create a new directory that will contain our generated apk
mkdir $HOME/CTFA/
#copy generated apk from build folder to the folder just created
cp -R CTF/build/outputs/apk/CTF-Android.apk $HOME/CTFA/
#go to home and setup git
cd $HOME
git config --global user.email "me@allanwang.ca"
git config --global user.name "Allan Wang"
#clone the repository in the buildApk folder
git clone --quiet --branch=master  https://AllanWang:$GITHUB_API_KEY@github.com/CTFMcGill/CTFMcGill.github.io.git  master > /dev/null
#go into directory and copy data we're interested
cd master
cp -Rf $HOME/CTFA/* .

API_JSON=$(printf '{"tag_name": "v%s","target_commitish": "master","name": "v%s","body": "Release of version %s","draft": false,"prerelease": false}' $VERSION $VERSION $VERSION)
curl --data "$API_JSON" https://api.github.com/repos/:owner/:repository/releases?access_token=:access_token

#add, commit and push files
git remote rm origin
git remote add origin https://AllanWang:$GITHUB_API_KEY@github.com/CTFMcGill/CTFMcGill.github.io.git
git add -f .
git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed [skip ci]"
git push -fq origin master > /dev/null
echo -e "Done\n"