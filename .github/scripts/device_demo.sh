#!/bin/bash
# Runs every app's UI test on the emulator, recording the screen and keeping screenshots.
set -u
OUT=device-output
mkdir -p "$OUT/screenshots" "$OUT/videos"
FAILED=0

adb wait-for-device
adb shell 'while [ "$(getprop sys.boot_completed)" != "1" ]; do sleep 1; done'

# Clean status bar for the screenshots (System UI demo mode).
adb shell settings put global sysui_demo_allowed 1
adb shell am broadcast -a com.android.systemui.demo -e command enter > /dev/null
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1000 > /dev/null
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false > /dev/null
adb shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4 -e mobile show -e datatype none -e level 4 > /dev/null
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false > /dev/null
adb shell settings put secure show_ime_with_hard_keyboard 1

# Sample contacts for Task 3 (fictional names and numbers).
seed_contact() {
  adb shell content insert --uri content://com.android.contacts/raw_contacts --bind account_type:n: --bind account_name:n:
  local id
  id=$(adb shell content query --uri content://com.android.contacts/raw_contacts --projection _id | tail -1 | sed -E 's/.*_id=([0-9]+).*/\1/')
  adb shell "content insert --uri content://com.android.contacts/data --bind raw_contact_id:i:$id --bind mimetype:s:vnd.android.cursor.item/name --bind 'data1:s:$1'"
  adb shell "content insert --uri content://com.android.contacts/data --bind raw_contact_id:i:$id --bind mimetype:s:vnd.android.cursor.item/phone_v2 --bind 'data1:s:$2' --bind data2:i:2"
  if [ -n "${3:-}" ]; then
    adb shell "content insert --uri content://com.android.contacts/data --bind raw_contact_id:i:$id --bind mimetype:s:vnd.android.cursor.item/phone_v2 --bind 'data1:s:$3' --bind data2:i:1"
  fi
}
seed_contact "Amal Perera" "+94 77 210 4411"
seed_contact "Ayesha Fernando" "+94 71 384 2290" "011 245 7781"
seed_contact "Dinesh Kumar" "+94 76 552 0193"
seed_contact "Fathima Rizna" "+94 75 908 3362"
seed_contact "Hasini Silva" "+94 77 640 1158"
seed_contact "Kavindu Jayasinghe" "+94 70 223 9047"
seed_contact "Mohamed Arshad" "+94 77 815 6620"
seed_contact "Nimali Wickrama" "+94 72 431 7785" "065 222 4410"
seed_contact "Priya Rajan" "+94 76 109 8834"
seed_contact "Sahan Dissanayake" "+94 71 760 2251"
seed_contact "Tharindu Bandara" "+94 78 347 6609"
seed_contact "Zainab Ismail" "+94 75 288 1940"

run_module() {
  local module=$1 pkg=$2
  echo "::group::$module"
  adb install -r -t "$module/build/outputs/apk/debug/$module-debug.apk"
  adb install -r -t "$module/build/outputs/apk/androidTest/debug/$module-debug-androidTest.apk"
  adb shell input keyevent KEYCODE_HOME
  sleep 1
  adb shell screenrecord --bit-rate 8000000 "/sdcard/$module.mp4" &
  local rec=$!
  sleep 1
  local result
  result=$(adb shell am instrument -w "$pkg.test/androidx.test.runner.AndroidJUnitRunner" 2>&1)
  echo "$result"
  sleep 1
  adb shell pkill -INT screenrecord || true
  wait $rec 2>/dev/null || true
  sleep 2
  adb pull "/sdcard/$module.mp4" "$OUT/videos/$module.mp4" || true
  if ! echo "$result" | grep -q "^OK ("; then
    echo "::error::$module UI test failed"
    FAILED=1
  fi
  echo "::endgroup::"
}

run_module Task1_QuizApp com.risnysuha.saiket.quiz
run_module Task2_ImageGallery com.risnysuha.saiket.gallery
run_module Task3_ContactsList com.risnysuha.saiket.contacts
run_module Task4_ActivitySwitch com.risnysuha.saiket.activityswitch
run_module Task5_TimerApp com.risnysuha.saiket.timer

adb pull /sdcard/Pictures/saiket/. "$OUT/screenshots/" || true
ls -la "$OUT/screenshots" "$OUT/videos"
exit $FAILED
