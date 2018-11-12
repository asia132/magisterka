help: 
	echo run - prints the names of method signatures for java classes
	echo clean - prints the names of method signatures for java classes

# MAIN - the name of java file, which contains main function
# PACKAGE - name of the class package
MAIN = Main
PACKAGE = grammar_graphs

#JC - name of java compilator
JC = javac

#.SUFFIXES - defines default list of suffixes, before make reads any makefile
#.PHONY - defines targets, which are not a files
.SUFFIXES: .java .class .html
.PHONY: run, clean, doc, open_doc

#cleans all files, created during building program
clean:	
	$(RM) ./$(PACKAGE)/*.class
	$(RM) $(MAIN).class

build:
	@$(JC) $(MAIN).java

run:
	@java $(MAIN)

r: 
	$(RM) ./$(PACKAGE)/*.class
	$(RM) $(MAIN).class
	@$(JC) $(MAIN).java
	@java $(MAIN)

#generates the documentation
doc:
	javadoc $(PACKAGE) -d ./doc

#opens the documentation
open_doc:
	firefox ./doc/$(PACKAGE)/$(J_NAME).html

git_push:
	git add -A
	git commit -m 'update'
	git push origin master

git_pull:
	git pull origin master
