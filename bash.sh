#How to run
#su
#chmod +x /path/to/bash.sh
#/path/to/bash.sh

let width=720;
let height=1280;

let b1=90
let b2=270
let b3=450
let b4=630

while true
do
	/system/bin/screencap /storage/emulated/0/Download/img.dump
	a1=$(dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360090 2>/dev/null | hd)
	a2=$(dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360270 2>/dev/null | hd)
	a3=$(dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360450 2>/dev/null | hd)
	a4=$(dd if='/storage/emulated/0/Download/img.dump' bs=4 count=1 skip=360630 2>/dev/null | hd)

	if [[ $a1 == *"00 00 00 ff"* ]]; then
	  /system/bin/input tap 500 90
	fi

	if [[ $a2 == *"00 00 00 ff"* ]]; then
	  /system/bin/input tap 500 270
	fi

	if [[ $a3 == *"00 00 00 ff"* ]]; then
	  /system/bin/input tap 500 450
	fi

	if [[ $a4 == *"00 00 00 ff"* ]]; then
	  /system/bin/input tap 500 630
	fi
done