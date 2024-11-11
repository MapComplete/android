#! /bin/bash

cd MapComplete

echo '{"oauth_landing": "https://app.mapcomplete.org/land.html"}' > config.json
# npm run prepare:deploy

echo '''
import type { CapacitorConfig } from "@capacitor/cli";

const config: CapacitorConfig = {
  appId: "org.mapcomplete",
  appName: "MapComplete",
  webDir: "dist-full"
};

export default config;
''' > capacitor.config.ts

rm -rf dist-full
mkdir dist-full
cp dist/*.html dist-full/
cp dist/*.css dist-full/
# cp dist/*.webmanifest dist-full/
cp -r dist/css dist-full/

mkdir dist-full/assets
cp dist/assets/*.js dist-full/assets
cp dist/assets/*.svg dist-full/assets
cp dist/assets/*.woff dist-full/assets
cp dist/assets/*.ttf dist-full/assets
cp dist/assets/*.png dist-full/assets
cp dist/assets/*.json dist-full/assets
cp dist/assets/*.css dist-full/assets

cp -r dist/assets/data dist-full/assets
cp -r dist/assets/docs dist-full/assets
cp -r dist/assets/fonts dist-full/assets
cp -r dist/assets/langs dist-full/assets
cp -r dist/assets/layers dist-full/assets
cp -r dist/assets/png dist-full/assets
cp -r dist/assets/svg dist-full/assets
cp -r dist/assets/templates dist-full/assets
cp -r dist/assets/themes dist-full/assets

mkdir dist-full/assets/generated
# cp dist/assets/generated/ dist-full/assets/generated

# npx cap add android
npx cap sync
# npx cap build android --keystorepath /home/pietervdvn/Documents/Data-backups/MapCompleteAndroidKeystore/mainkey.jks --keystorealias mapcomplete-key --keystorealiaspass "$1" --keystorepass "$1"
