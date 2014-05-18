#
# Postgres Database Dockerfile for use by the forked petclinic application
# available here - https://github.com/influenza/grails-petclinic
FROM nornagon/postgres

# Create the grails user and petclinic database
RUN /etc/init.d/postgresql start; \
    psql --command "CREATE USER grails WITH SUPERUSER PASSWORD 'super secure';";\
    createdb --owner=grails petclinic "Pet clinic data storage"

# Set the default command so that our container acts like a well behaved binary
CMD ["/usr/lib/postgresql/9.3/bin/postgres", "-D", \
    "/var/lib/postgresql/9.3/main", "-c", \
    "config_file=/etc/postgresql/9.3/main/postgresql.conf"]

