#!/bin/bash

SUBJECT="$( rgrep -F "dmx.sendmail.greeting_subject" dmx/ \
    | grep -v $( basename $0 ) \
    | grep -v '<The subject for the greeting email>' \
    | awk -F':' '{ print $2 }' \
    | cut -d'=' -f2  \
    | sed 's/^[ \t]*//g' \
    )"
RESULT="$( curl -sS https://${WEB_URL}/mails/api/v2/messages \
    | jq .items[0].Content.Headers.Subject \
    | grep -vE "\[|\]" \
    | sed 's/\"//g' \
    | sed 's/^[ \t]*//g' \
    | grep "${SUBJECT}" \
    )"
if [ ! -z "${SUBJECT}" ] && [ ! -z "${RESULT}" ] && [ "${SUBJECT}" == "${RESULT}" ]; then
    echo "INFO: Found greeting mail with subject '${SUBJECT}'. Test passed."
else
    echo "ERROR! No greeting mail found (SUBJECT=${SUBJECT}). Test failed."
    exit 1
fi

