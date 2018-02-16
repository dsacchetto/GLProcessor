#!/bin/sh

# Template in our config

set > /START_ENVIRONMENT

# echo "Running confd to template the config file..."
# /usr/local/bin/confd --onetime --log-level debug --config-file /etc/confd/confd.toml
# echo "... done"

echo "Starting supervisor..."
supervisord -c /etc/supervisor/supervisord.conf -n 
