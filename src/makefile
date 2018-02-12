# makkefile begins

JFLAGS = -g -encoding ISO-8859-1
JC = javac
JVM= java 

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        Compteur.java \
        DessinGrille.java \
        IntBej.java \
        JeuBej.java \
        JoueurVirtuel.java

default: classes

# Next line is a target dependency line
# This target entry uses Suffix Replacement within a macro: 
# $(macroname:string1=string2)
# In the words in the macro named 'macroname' replace 'string1' with 'string2'
# Below we are replacing the suffix .java of all words in the macro CLASSES 
# with the .class suffix
#

classes: $(CLASSES:.java=.class)


# Next two lines contain a target for running the program
# Remember the tab in the second line.
# $(JMV) y $(MAIN) are replaced by their values

# MAIN = Experiment 
# run: $(MAIN).class
# 	$(JVM) $(MAIN)

# Run the applet
run: test.html
	appletviewer test.html

# this line is to remove all unneeded files from
# the directory when we are finished executing(saves space)
# and "cleans up" the directory of unneeded .class files
# RM is a predefined macro in make (RM = rm -f)
#

clean:
	$(RM) *.class
