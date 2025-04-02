ALES="xurbana00"
MARTIN="xkovacm01" # Vedoucí
JUNIT="junit-platform-console-standalone-1.11.4.jar"

.PHONY: clean zip build run test

build:
	mvn clean package

run:
	mvn javafx:run

test:
	mvn test

clean:
	rm -rf ${MARTIN}.zip target/
	mvn clean

zip:
	zip ${MARTIN}.zip -r rozdeleni.txt requirements.pdf readme.txt pom.xml src/ -x "*.DS_Store"

all: build run
