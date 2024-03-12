#!/bin/bash

LOG_PREFIX="[SH >]"

echo "${LOG_PREFIX} Checking untracked changes..."
if ! git diff-index --quiet HEAD --
then
    echo "Please commit your changes before running this script!"
    exit 1
fi

echo

echo "${LOG_PREFIX} Checking github auth status..."
gh auth status

echo

echo "${LOG_PREFIX} Getting latest release..."
TAG=$(gh release list --json tagName,isLatest --jq ".[] | select( .isLatest == true) | .tagName")
echo "${TAG}"

echo

echo "${LOG_PREFIX} Bump up version..."
RELEASE=$(echo "${TAG}" | awk -F. '/[0-9]+\./{$NF++;print}' OFS=.)
echo "${RELEASE}"

echo

read -p "${LOG_PREFIX} Are you sure you want to create this release (${RELEASE})? (yYN) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    echo "Release canceled."
    exit 1
fi

echo

echo "${LOG_PREFIX} Creating new release in github..."
gh release create "${RELEASE}" --generate-notes
