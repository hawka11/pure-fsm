#!/bin/bash

ansible-playbook --inventory-file=./inv_default -u vagrant --private-key=~/.vagrant.d/insecure_private_key ./site.yml