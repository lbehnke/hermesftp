#! /bin/sh
keytool -genkey -alias hermes -keyalg DSA -keystore keystore -validity 365 -storepass secret -keypass secret
