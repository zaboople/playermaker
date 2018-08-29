# This is just a shortcut script for running the on-screen-keyboard application:
cd $(dirname $0) || exit 1
java -classpath build "$@" org.tmotte.keyboard.Main -i D:/troy/dev/FluidR3_GM.SF2 &
#java -classpath build "$@" org.tmotte.keyboard.Main &
