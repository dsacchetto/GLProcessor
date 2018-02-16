FROM spireon/spireonapp:latest
ARG GIT_COMMIT=unspecified
ARG SERVICE_NAME=unspecified

LABEL maintainer="Benjamin Rizkowsky <brizkowsky@spireon.com>"
LABEL git_commit=$GIT_COMMIT
LABEL servicename=$SERVICE_NAME


COPY build/GLProcessor-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/spireonapp/bin/

ADD supervisor/spireonapp.conf /etc/supervisor/conf.d/spireonapp.conf
ADD build /opt/spireonapp/bin
RUN chown -R spireonapp:spireonapp /opt/spireonapp
