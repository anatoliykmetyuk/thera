#!/usr/bin/env bash

HELP="Usage: thera <command>, where <command> is one of:
  start - Start Thera
  stop  - Stop Thera
  bash  - open bash console at Thera's Docker image"

SELF_DIR=`pwd`
COMMAND_OVERRIDES="$SELF_DIR/thera.sh"
IMAGE_OVERRIDE="$SELF_DIR/Dockerfile"
DEFAULT_IMAGE_NAME=thera:latest

function start_thera {
  if [ -a $IMAGE_OVERRIDE ]; then
    if ! [ -z ${IMAGE_NAME+x} ]; then  # https://stackoverflow.com/a/13864829
      echo "Dockerfile override active. Building image with name $IMAGE_NAME"
      docker build -t $IMAGE_NAME .
    else
      echo "Dockerfile detected, but IMAGE_NAME variable is not set.
Using standard image, thera:latest. Please set IMAGE_NAME environment variable in thera.sh file
to name your image and use it instead of the standard one."
      IMAGE_NAME=$DEFAULT_IMAGE_NAME
    fi
  else
    IMAGE_NAME=$DEFAULT_IMAGE_NAME
  fi

  docker run -td \
    -v $SELF_DIR/_volumes/home:/root \
    -v $SELF_DIR:/root/thera \
    -v /Users/anatolii/.ivy2:/root/.ivy2 \
    -p 8888:8888 \
    --name thera \
    --rm \
    $IMAGE_NAME
}

function stop_thera {
  docker stop thera
}

function run_on_thera {
  docker exec -ti thera $@
}

if [ -a $COMMAND_OVERRIDES ]; then
  echo "Loading functions from $COMMAND_OVERRIDES"
  . $COMMAND_OVERRIDES
fi

case $1 in
    start) start_thera;;
     stop) stop_thera;;
  restart) stop_thera; start_thera;;

  build) run_on_thera amm build.sc;;
   bash) run_on_thera bash;;

    '') echo -e "$HELP";;
     *) echo -e "Unknown command: $1\n$HELP";;
esac
