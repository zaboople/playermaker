cd $(dirname $0) || exit 1
ant clean compile && java -classpath build "$@"