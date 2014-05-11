Migrating from Vagrant to Docker
================================

This files are to supplement my blog post [of the same name](http://link). Provided
here are the following files:

* Vagrant/ - The vagrant version of our test app
  - Vagrantfile - Specifies the virtual machine
  - Ansible/ - Contains provisioning artifacts
    - hosts.ini - A host inventory file
    - playbook.yml - Will provision a machine for running a sample grails app
* Docker/