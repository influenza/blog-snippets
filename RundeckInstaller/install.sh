#!/bin/bash
TMP_DIR=`mktemp -d`
PKG_NAME="rundeck-1.5.2-1-GA.deb"
PASSWORD=`dd if=/dev/urandom bs=32 count=1 2>/dev/null | base64`
pushd $TMP_DIR
# Grab the release
wget http://download.rundeck.org/deb/$PKG_NAME
# Verify the SHA sum
shasum $PKG_NAME | grep -w 987f662b0b91b882865cb719e6b1bb603314c52d
if [ $? -ne 0 ]
then
	echo "shasum doesn't match for $PKG_NAME, refusing to install"
	exit -1
fi
# Install it!
sudo dpkg -i $PKG_NAME
# Patch the authentication file
pushd /etc/rundeck/
echo "Patching password file"
sudo patch <<ENDPATCH
--- realm.properties  2013-05-15 19:18:06.472012497 +0000
+++ realm.properties.patched  2013-05-15 19:18:42.470361173 +0000
@@ -22,6 +22,6 @@
 #
 # This sets the default user accounts for the Rundeck app 
 #
-admin:admin,user,admin,architect,deploy,build
+admin:$PASSWORD,user,admin,architect,deploy,build
 #@jetty.user.deploy.name@:@jetty.user.deploy.password@,user,deploy
 #@jetty.user.build.name@:@jetty.user.build.password@,user,build
ENDPATCH
popd # return to tmp dir
echo "Installation complete. Starting service..."
sudo /etc/init.d/rundeckd start
# clean up temp folder
popd
rm -Rf $TMP_DIR
echo "Service started. Rundeck should be available at http://localhost:4440"
echo "*********************************************************************"
echo "Your admin password has been set to: $PASSWORD"
echo "*********************************************************************"
echo "This can be changed by editing /etc/rundeck/realm.properties"
echo "See http://rundeck.org/1.5.2/administration/authentication.html for more info"
