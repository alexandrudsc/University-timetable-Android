#!/bin/bash
# generates and android raw file with the commit messages which start with
# [number] in the commit message
# If the commit limits are not set, it will go back until the last tag



# ============================================================================ #
# Print help
# ============================================================================ #
function do_generate() {
	version=`cat VERSION` &&
	from=${2:-master} &&
	to=${3:-HEAD} &&
	grep_args="^\[[0-9]\+\]"
	file=app/src/main/res/raw/v$version
	sed -i "s/version \=.*/version \= \"$version\"/" \
	 "app/src/main/java/com/developer/alexandru/orarusv/changelog/"\
"ChangelogActivity.kt" &&
	date=$(date '+%Y-%b-%d') &&
	echo "v$version ($date)" > $file &&
	git log --abbrev-commit --pretty=oneline --format="%s; by %ce" \
	 $from..$to --grep=$grep_args >> $file &&
	git diff $from..$to '--grep=$grep_args' \
	 --shortstat >> $file

	return $?
}



# ============================================================================ #
# Print help
# ============================================================================ #
function print_help() {
  echo "--generate [from] [to]   Generates a changelog file"
}



# ============================================================================ #
# Main
# ============================================================================ #

# If no parameter
if [ $# == 0 ]; then
  print_help
fi

# Case
if [ $1 ]; then
  case "$1" in
  	--generate) do_generate "$@" ; exit $? ;; 
    --help) print_help ; exit $? ;;
    *) print_help ; exit $? ;;
  esac
fi
