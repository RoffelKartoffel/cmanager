#!/bin/bash
find ./src/ -iname *.java | xargs clang-format -i
find ./test/ -iname *.java | xargs clang-format -i
