#!/bin/bash
find ./src/ -iname *.java | xargs clang-format -i
