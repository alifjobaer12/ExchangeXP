#!/usr/bin/env bash
set -euo pipefail

# Expecting two environment variables:
# FIREBASE_SA_ENC  => the base64 encrypted content (one long string; can be multiline)
# FIREBASE_SA_KEY  => the passphrase to decrypt

echo "DEBUG: FIREBASE_SA_ENC length: $(printf '%s' "$FIREBASE_SA_ENC" | wc -c)"
echo "DEBUG: FIREBASE_SA_KEY length: $(printf '%s' "$FIREBASE_SA_KEY" | wc -c)"


if [ -z "${FIREBASE_SA_ENC:-}" ] || [ -z "${FIREBASE_SA_KEY:-}" ]; then
  echo "ERROR: FIREBASE_SA_ENC and FIREBASE_SA_KEY must be set."
  echo "If running locally, export them or use the .env file (do NOT commit it)."
  exit 1
fi

# Write encrypted content to a temp file (openssl reads base64 input)
TMP_ENC="/tmp/firebase.json.enc"
TMP_JSON="/tmp/firebase.json"

# Ensure no trailing weird characters; preserve newlines
printf "%s" "$FIREBASE_SA_ENC" > "$TMP_ENC"

# Decrypt into /tmp/firebase.json
openssl enc -aes-256-cbc -d -base64 -in "$TMP_ENC" -out "$TMP_JSON" -pass pass:"$FIREBASE_SA_KEY"

chmod 600 "$TMP_JSON"

# Point Firebase Admin SDK to the file
export FIREBASE_JSON="$TMP_JSON"

# (Optional) Print confirmation (do NOT print secret contents)
echo "Firebase credentials written to $FIREBASE_JSON"

# Start the Spring Boot application (adjust jar name if different)
# If you run with mvn spring-boot:run skip this and call mvn; for a jar use the line below:
exec java -jar /app/app.jar

