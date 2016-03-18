FROM centos:centos7

RUN yum -y install wget \
&& wget -O jre.rpm --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jre-8u60-linux-x64.rpm \
&& yum -y localinstall jre.rpm \
&& rm jre.rpm \
&& yum -y install nmap
