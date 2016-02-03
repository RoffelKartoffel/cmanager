#!/bin/bash
bfg --delete-files 'cm-0.{*}.zip'
git reflog expire --expire=now --all && git gc --prune=now --aggressive
git push origin --force --all
