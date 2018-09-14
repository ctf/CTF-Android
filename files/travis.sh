#!/usr/bin/env bash

# Add appropriate files for encryption

rm ctf.tar.enc
cd ..
tar cvf ctf.tar files/release.keystore files/release.properties
travis encrypt-file ctf.tar --add
rm ctf.tar
mv ctf.tar.enc files/