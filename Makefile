.PHONY: all
all: install

DEST := $(HOME)/projects/personal/dotfiles/bin/bin/

install:
	cp -a target/uberjar/hs.jar $(DEST)

build:
	lein uberjar
