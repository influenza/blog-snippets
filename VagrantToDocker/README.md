Migrating from Vagrant to Docker
================================

This files are to supplement my blog post [of the same name](http://dahlgren.so/software/2014/05/11/From-Vagrant-To-Docker/). 
Provided here are the following files:

* Vagrant/ - The vagrant version of our test app
  - Vagrantfile - Specifies the virtual machine
  - Ansible/ - Contains provisioning artifacts
    - hosts.ini - A host inventory file
    - playbook.yml - Will provision a machine for running a sample grails app
* Docker/
  - Dockerfile - Specifies the container we use
