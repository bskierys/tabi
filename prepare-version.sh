#!/bin/bash

changelog_file="CHANGELOG.md"
release_notes_file="release-notes.txt"
temp_file="TEMP.md"

msg() {
    echo -e "$@" > /dev/stderr
}

warn() {
    echo -e "${FB}${CY}WARNING:${F0} $@" > /dev/stderr
}

error_exit() {
    echo -e "${FB}${CR}ERROR:${F0} $@" > /dev/stderr
    exit 1
}

check_git_dir() {
    if ! git status --porcelain &> /dev/null ; then
        error_exit "Current dir is not a git repository!"
    fi
}

get_curr_branch() {
    branch=$(git rev-parse --abbrev-ref HEAD)
}

check_if_release() {
    get_curr_branch
    if ! echo $branch | grep -E 'release/.*' > /dev/null; then
        error_exit "$branch is not a release branch"  
    fi
}

get_curr_version() {
	get_curr_branch
	version=${branch#*"release/"}
	version="v${version}"
}

read_auth() {
    auth_token=$(<github_auth.priv)
}

generate_changelog() {
	github_changelog_generator -u bskierys -p tabi --token ${auth_token} --include-labels bug,feature,ci --output ${changelog_file} --no-verbose --future-release ${version}
}

generate_release_notes() {
	github_changelog_generator -u bskierys -p tabi --token ${auth_token} --include-labels fixed --exclude-labels ci,task,invalid,testing --output ${temp_file} --simple-list --no-verbose --future-release ${version}
	./edit_release_notes.rb -i ${temp_file} -o ${release_notes_file}
	rm ${temp_file}
}

check_git_dir
check_if_release
get_curr_version
msg "Generating changelog to version: ${version}"

read_auth
msg "Generating changelog"
generate_changelog
msg "Generating release notes"
generate_release_notes