FROM kyleaure/oracle-18.4.0-expanded:1.0.full

# Update and install Kerberos (server head)
USER root
RUN microdnf update
RUN microdnf -y install krb5-workstation krb5-libs

COPY setup/ /opt/oracle/scripts/setup
COPY startup/ /container-entrypoint-startdb.d/

ENV ORACLE_PWD=password
ENV KRB5_REALM=EXAMPLE.COM
ENV KRB5_KDC=kerberos

RUN mkdir /etc/krb5
RUN printf 'add_entry -password -p XE/oracle@EXAMPLE.COM -k 1 -e aes256-cts\npassword\nwkt /etc/krb5.keytab' | ktutil
RUN printf 'add_entry -password -p oracle@EXAMPLE.COM -k 1 -e aes256-cts\npassword\nwkt /etc/krb5.keytab' | ktutil

RUN chmod 777 /opt/oracle/scripts/setup/1kerberos.sh
RUN chmod 777 /opt/oracle/scripts/setup/2oracle.sh
RUN chmod 777 /container-entrypoint-startdb.d/01_kerberos.sh
RUN chmod 777 /container-entrypoint-startdb.d/02_oracle.sql

RUN /opt/oracle/scripts/setup/1kerberos.sh
RUN /opt/oracle/scripts/setup/2oracle.sh

RUN rm /opt/oracle/scripts/setup/1kerberos.sh
RUN rm /opt/oracle/scripts/setup/2oracle.sh 
 
USER oracle

# Currently tagged in DockerHub as: kyleaure/oracle-18.4.0-expanded:1.0.full.krb5
