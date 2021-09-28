.PHONY: all
all: build install

DEST := $(HOME)/projects/personal/dotfiles/bin/bin/
LEIN := $(HOME)/bin/lein

install:
	cp -a target/uberjar/hs.jar $(DEST)

build:
	$(LEIN) uberjar
