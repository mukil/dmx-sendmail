#!/bin/bash


##  variables:
if [ -z "${TIER}" ]; then
    export TIER='dev'
fi
if [ -z "${COMPOSE_PROJECT_NAME}" ]; then
    COMPOSE_PROJECT_NAME="${CI_PROJECT_NAME}_${CI_COMMIT_REF_SLUG}"
fi
if [ "${CI_COMMIT_BRANCH}" != "${CI_COMMIT_REF_SLUG}" ]; then
    echo "CI_COMMIT_BRANCH: ${CI_COMMIT_BRANCH}"
    echo "CI_COMMIT_REF_SLUG: ${CI_COMMIT_REF_SLUG}"
fi
if [ -z "${DEPLOY_PREFIX}" ] && [ "${CI_COMMIT_BRANCH}" == "master" -o "${CI_COMMIT_BRANCH}" == "main" ]; then
    DEPLOY_PREFIX="${CI_PROJECT_NAME}-${TIER}"
    export DEPLOY_PREFIX="${DEPLOY_PREFIX}"
elif [ -z "${DEPLOY_PREFIX}" ] && [ "${CI_COMMIT_BRANCH}" != "master" -a "${CI_COMMIT_BRANCH}" != "main" ]; then
    DEPLOY_PREFIX="${CI_COMMIT_REF_SLUG}_${CI_PROJECT_NAME}-${TIER}"
    export DEPLOY_PREFIX="${DEPLOY_PREFIX}"
fi

if [ -z "${WEB_URL}" ]; then
    export WEB_URL="${DEPLOY_PREFIX}.ci.dmx.systems"
fi
if [ -z "${CONFIG_DIR}" ]; then
    CONFIG_DIR='deploy/.config'
fi
if [ -z "${ENV_FILE}" ]; then
    ENV_FILE="${CONFIG_DIR}/.env.${CI_COMMIT_REF_SLUG}.ci"
fi
if [ -z "${WEBDIR}" ]; then
    WEBDIR='https://download.dmx.systems/ci'    # <= stable|latest
fi
if [ -z "${WEBCGI}" ]; then
    WEBCGI='https://download.dmx.systems/cgi-bin/v1/latest-version.cgi?'        # <= stable|latest
fi
if [ -z "${DOCKER_COMPOSE_PROFILE}" ]; then
    DOCKER_COMPOSE_PROFILE="${TIER}-ci"
fi

function mkpw() {
    local LEN="$( shuf -i12-16 -n1 )"
    local PW="$( tr -dc A-Za-z0-9 </dev/urandom | head -c 2 )"
    local PW="${PW}$( pwgen -c -n ${LEN} -1 -y -B -r \?\^\$\.\\~\`\'\*\%\:\<\>\=\/\#\{\}\|\!\@\[\]\"\&\(\) )"
    echo -n "${PW}"
}

if [ -z "${DMX_ADMIN_PASSWORD}" ]; then
    export DMX_ADMIN_PASSWORD="$( mkpw )"
    echo "DMX_ADMIN_PASSWORD=${DMX_ADMIN_PASSWORD}"
fi

if [ -z "${LDAP_ADMIN_PASSWORD}" ]; then
    export LDAP_ADMIN_PASSWORD="$( mkpw )$( mkpw )"
    echo "LDAP_ADMIN_PASSWORD=${LDAP_ADMIN_PASSWORD}"
fi
if [ -z "${DMX_DIRS}" ]; then
    declare -a DMX_DIRS=(conf logs db filedir bundle-deploy bundle-available)
fi
