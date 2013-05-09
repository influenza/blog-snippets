#!/bin/bash
#############################################################################
# This script assumes the following:
#		- it is being ran as a non-root sudoer
#		- aptitude is the package manager being used
#		- apache2 will be used as the web server (and is already installed)
#		- apache2 is controlled via service(8), rather than manual init scripts
############################################################################
# WARNING - This script will enable the default graphite site and restart
#		apache2 during its execution. If this is a problem, this script is not
#		for you!
############################################################################
MAJOR_VERSION="0.9"
VERSION="0.9.10"
# Ensure dependencies are met
sudo apt-get update
sudo apt-get --yes install python python-cairo-dev libapache2-mod-python \
	python-django python-ldap python-pysqlite2 libapache2-mod-wsgi \
	python-simplejson python-pip python-dev
sudo pip install twisted
sudo pip install django-tagging
# Make a temp directory
TMP_DIR=`mktemp -d`
pushd $TMP_DIR
wget https://launchpad.net/graphite/$MAJOR_VERSION/$VERSION/+download/graphite-web-$VERSION.tar.gz
wget https://launchpad.net/graphite/$MAJOR_VERSION/$VERSION/+download/carbon-$VERSION.tar.gz
wget https://launchpad.net/graphite/$MAJOR_VERSION/$VERSION/+download/whisper-$VERSION.tar.gz
wget https://launchpad.net/graphite/$MAJOR_VERSION/$VERSION/+download/check-dependencies.py

# Unpack the tarballs
for component in graphite-web carbon whisper 
do
	tar -zxf $component-$VERSION.tar.gz
done

# Patch check-dependencies to set error status on fatal conditions
patch <<ENDPATCH
--- check-dependencies.py	2012-05-03 14:14:33.000000000 +0000
+++ check-dependencies.py-fixed	2013-05-09 21:51:46.414367464 +0000
@@ -170,6 +170,7 @@
 
 if fatal:
   print "%d necessary dependencies not met. Graphite will not function until these dependencies are fulfilled." % fatal
+  raise SystemExit(fatal)
 
 else:
   print "All necessary dependencies are met."
ENDPATCH

# Install Whisper
pushd whisper-$VERSION
sudo python setup.py install
popd

# Install Carbon
pushd carbon-$VERSION
sudo python setup.py install
popd

# Configure Carbon
pushd /opt/graphite/conf
sudo cp carbon.conf.example carbon.conf
sudo cp storage-schemas.conf.example storage-schemas.conf
sudo cp graphite.wsgi.example graphite.wsgi
popd

# Verify dependencies are met
./check-dependencies.py

# Configure webapp
pushd graphite-web-$VERSION
if [ $? -ne 0 ]
then
	echo "Some dependencies are missing. Check the output above"
	sudo rm -Rf $TMP_DIR # Clean up temp directory
	exit 1
fi
sudo python setup.py install
# Copy apache2 config over and enable the graphite site
sudo cp ./examples/example-graphite-vhost.conf /etc/apache2/sites-available/graphite
popd
# Patch broken vhost file
pushd /etc/apache2/sites-available
sudo patch <<ENDPATCH
--- graphite  2013-05-09 21:16:27.842684058 +0000
+++ graphite-fixed  2013-05-09 21:28:31.870334135 +0000
@@ -18,6 +18,8 @@
 # XXX You need to set this up! 
 # Read http://code.google.com/p/modwsgi/wiki/ConfigurationDirectives#WSGISocketPrefix
 WSGISocketPrefix run/wsgi
+WSGIImportScript /opt/graphite/conf/graphite.wsgi process-group=graphite application-group=%{GLOBAL}
+
 
 <VirtualHost *:80>
         ServerName graphite
@@ -30,7 +32,6 @@
         WSGIDaemonProcess graphite processes=5 threads=5 display-name='%{GROUP}' inactivity-timeout=120
         WSGIProcessGroup graphite
         WSGIApplicationGroup %{GLOBAL}
-        WSGIImportScript /opt/graphite/conf/graphite.wsgi process-group=graphite application-group=%{GLOBAL}
 
         # XXX You will need to create this file! There is a graphite.wsgi.example
         # file in this directory that you can safely use, just copy it to graphite.wgsi
ENDPATCH
# Determine what user apache2 is running as... this won't work for everyone
APACHE_USR=`ps aux | grep '[a]pache2' | cut -d ' ' -f 1 | grep -v root | head -1`
APACHE_GRP=`id -g $APACHE_USR`
# Create the run directory if it doesn't exist already
sudo mkdir /etc/apache2/run
sudo chown $APACHE_USR:$APACHE_GRP /etc/apache2/run
sudo a2ensite graphite
sudo service apache2 reload
if [ $? -ne 0 ]
then
	echo "Failed to reload apache2."
	echo "There is likely an error in /etc/apache2/sites-available/graphite"
	sudo rm -Rf $TMP_DIR # Clean up temp directory
	exit 1
fi
popd

# Initial database creation
pushd /opt/graphite/webapp/graphite
sudo python manage.py syncdb --noinput
popd

# Change permissions so the apache2 user can operator on the graphite web files
sudo chown -R $APACHE_USR:$APACHE_GRP /opt/graphite/storage
# Restart apache2
sudo service apache2 restart

# Start Carbon
pushd /opt/graphite
sudo su $APACHE_USR -c './bin/carbon-cache.py start'
popd

popd
# Clean up the temp directory 
sudo rm -Rf $TMP_DIR 

echo "Installation complete. Verify the install with /opt/graphite/examples/example-client.py"
