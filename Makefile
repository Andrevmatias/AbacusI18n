# Makefile for Java things

wIDGET=abacus
WIDGET=Abacus
DOMAIN=tux.org
USER=bagleyd
PACKAGES=org/tux/$(USER)

APPLET=$(WIDGET)Applet
APP=$(WIDGET)App

# Ubuntu use: "apt-get install" and "update-java-alternatives"
CHECK_OPT=-deprecation -Xlint
VER=1.7
COMPLIANCE_OPT=-source $(VER) -target $(VER)
#JAVA_HOME=/usr/lib/jvm/java-gcj
#JAVA_HOME=/usr/lib/jvm/java-6-sun
#JAVA_HOME=/usr/lib/jvm/java-7-icedtea
#JAVA_HOME="C:/Program\ Files/Java/jdk1.7.0"
#JAVA_HOME="/cygdrive/c/Program\ Files/Java/jdk1.7.0"
#CLASSPATH_OPT=-classpath $(JAVA_HOME)/jre/lib/plugin.jar:.
JAVA_OPT=$(CHECK_OPT) $(COMPLIANCE_OPT) $(CLASSPATH_OPT)
PLUGIN=C:/Program Files/Java/jre7/lib/plugin.jar

# DOS
#J=.jav
#C=.cla
#H=.htm
#S=.sco

# OTHER
J=.java
C=.class
H=.html
X=.xpm
P=.png
A=.au
W=.wav

VPATH=classes
DEST=$(VPATH)
SRC=src
RES=res

CLASSES=\
	$(PACKAGES)/util/ArgumentParser$(C)\
	$(PACKAGES)/util/Colour$(C)\
	$(PACKAGES)/util/ColumnLayout$(C)\
	$(PACKAGES)/util/ComponentUtil$(C)\
	$(PACKAGES)/util/Functions$(C)\
	$(PACKAGES)/util/Icon$(C)\
	$(PACKAGES)/util/MultiLineLabel$(C)\
	$(PACKAGES)/util/OKDialog$(C)\
	$(PACKAGES)/util/OrientDraw$(C)\
	$(PACKAGES)/util/PartialDisableComboBox$(C)\
	$(PACKAGES)/util/Slider$(C)\
	$(PACKAGES)/util/TextPrompt$(C)\
	$(PACKAGES)/$(wIDGET)/learn/$(WIDGET)Demo$(C)\
	$(PACKAGES)/$(wIDGET)/learn/$(WIDGET)Exam$(C)\
	$(PACKAGES)/$(wIDGET)/learn/$(WIDGET)Problem$(C)\
	$(PACKAGES)/$(wIDGET)/learn/$(WIDGET)Teach$(C)\
	$(PACKAGES)/$(wIDGET)/learn/$(WIDGET)Test$(C)\
	$(PACKAGES)/$(wIDGET)/learn/CountdownTimer$(C)\
	$(PACKAGES)/$(wIDGET)/learn/DemoDialog$(C)\
	$(PACKAGES)/$(wIDGET)/learn/TeachDialog$(C)\
	$(PACKAGES)/$(wIDGET)/learn/TestDialog$(C)\
	$(PACKAGES)/$(wIDGET)/model/$(WIDGET)Deck$(C)\
	$(PACKAGES)/$(wIDGET)/model/$(WIDGET)Format$(C)\
	$(PACKAGES)/$(wIDGET)/model/$(WIDGET)Subdeck$(C)\
	$(PACKAGES)/$(wIDGET)/model/NumberField$(C)\
	$(PACKAGES)/$(wIDGET)/view/$(WIDGET)Draw$(C)\
	$(PACKAGES)/$(wIDGET)/view/$(WIDGET)Geometry$(C)\
	$(PACKAGES)/$(wIDGET)/$(WIDGET)Calc$(C)\
	$(PACKAGES)/$(wIDGET)/$(WIDGET)Canvas$(C)\
	$(PACKAGES)/$(wIDGET)/$(WIDGET)Interface$(C)\
	$(PACKAGES)/$(wIDGET)/$(WIDGET)Math$(C)\
	$(PACKAGES)/$(wIDGET)/$(WIDGET)$(C)\
	$(PACKAGES)/$(wIDGET)/$(APPLET)$(C)

ICONS=\
	16x16/$(wIDGET)$(P)\
	16x16/$(wIDGET)ru$(P)\
	16x16/$(wIDGET)dk$(P)\
	16x16/$(wIDGET)ro$(P)\
	16x16/$(wIDGET)me$(P)\
	32x32/$(wIDGET)$(P)\
	32x32/$(wIDGET)ru$(P)\
	32x32/$(wIDGET)dk$(P)\
	32x32/$(wIDGET)ro$(P)\
	32x32/$(wIDGET)me$(P)\
	48x48/$(wIDGET)$(P)\
	48x48/$(wIDGET)jp$(P)\
	48x48/$(wIDGET)ko$(P)\
	48x48/$(wIDGET)ru$(P)\
	48x48/$(wIDGET)dk$(P)\
	48x48/$(wIDGET)ro$(P)\
	48x48/$(wIDGET)me$(P)

ALL_ICONS=$(ICONS)\
	22x22/$(wIDGET)$(P)\
	24x24/$(wIDGET)$(P)\
	36x36/$(wIDGET)$(P)\
	64x64/$(wIDGET)$(P)\
	64x64/$(wIDGET)jp$(P)\
	64x64/$(wIDGET)ko$(P)\
	64x64/$(wIDGET)ge$(P)\
	64x64/$(wIDGET)ru$(P)\
	64x64/$(wIDGET)ro$(P)\
	64x64/$(wIDGET)dk$(P)\
	64x64/$(wIDGET)ma$(P)\
	64x64/$(wIDGET)me$(P)\
	64x64/$(wIDGET)ba$(P)\
	64x64/$(wIDGET)cn12$(P)\
	64x64/$(wIDGET)16$(P)\
	64x64/$(wIDGET)leecne$(P)\
	64x64/$(wIDGET)leecnn$(P)\
	64x64/$(wIDGET)leekon$(P)\
	64x64/$(wIDGET)oldru$(P)\
	72x72/$(wIDGET)$(P)\
	96x96/$(wIDGET)$(P)\
	144x144/$(wIDGET)$(P)

AU=\
	bump$(A)\
	move$(A)\
	drip$(A)

JS=\
	netscape/javascript/JSException$(C)\
	netscape/javascript/JSObject$(C)\
	netscape/javascript/JSUtil$(C)

SOURCES=$(CLASSES:%$(C)=$(SRC)/%$(J))

TARGETS=$(CLASSES:%=$(DEST)/%)

PNGS=$(ICONS:%$(P)=icons/%$(P))

RES_PNGS=$(ICONS:%$(P)=$(RES)/icons/%$(P))

DEST_PNGS=$(ICONS:%$(P)=$(DEST)/icons/%$(P))

ALL_PNGS=$(ALL_ICONS:%$(P)=$(RES)/icons/%$(P))

ALL_XPMS=$(ALL_ICONS:%$(X)=$(RES)/icons/%$(X))

AUS=$(AU:%$(A)=sounds/%$(A))

RES_AUS=$(AU:%$(A)=$(RES)/sounds/%$(A))

DEST_AUS=$(AU:%$(A)=$(DEST)/sounds/%$(A))

RES_JS=$(JS:%$(C)=$(RES)/%$(C))

DEST_JS=$(JS:%$(C)=$(DEST)/%$(C))

RESOURCES=$(DEST_PNGS) $(DEST_AUS) $(DEST_JS)

PACKAGE=$(wIDGET)/Makefile $(wIDGET)/build.xml $(wIDGET)/build.properties\
	$(wIDGET)/*$(H) $(wIDGET)/$(SRC)/$(PACKAGES)/*\
	$(wIDGET)/$(RES)/icons/*/$(wIDGET)*$(X)\
	$(wIDGET)/$(RES)/icons/*/$(wIDGET)*$(P)\
	$(wIDGET)/$(RES)/icons/mouse*$(P) $(wIDGET)/$(RES)/sounds/*$(A) \
	$(wIDGET)/$(RES)/netscape/javascript/*$(C)\
	$(wIDGET)/tests/*.txt $(wIDGET)/bin/*.sh

PACKAGEASCII=Makefile build.xml build.properties\
	*$(H) $(SOURCES) $(ALL_XPMS)

PACKAGEBINARY=$(PNGS) $(AUS) $(JS)

JAVAC=javac $(JAVA_OPT)

SHELL=/bin/sh
RM=rm -f
# RM=del
NICE=nice

all : package

rebuild : clean all

prepare : $(ALL_PNGS) $(RES_PNGS) $(RES_AUS) $(RES_JS)

init : $(DEST_PNGS) $(DEST_AUS) $(DEST_JS)

compile : prepare init $(TARGETS)

furnish : init $(RESOURCES)

package : compile furnish jar

deploy : package

$(RES)/icons/%$(P) : $(RES)/icons/%$(X)
	if grep None $< > /dev/null; then\
		sed -e 's/None/White/' $< | xpmtoppm |\
		pnmtopng -transparent 1,1,1 > $@;\
	else\
		xpmtoppm $< | pnmtopng > $@;\
	fi

icon : $(RES_PNGS)

sound : $(RES_AUS)

$(RES)/netscape/javascript/%$(C) :
	cd $(RES); jar xvf "$(PLUGIN)" netscape/javascript

$(DEST)/%$(C) : $(SRC)/%$(J)
	$(JAVAC) $< -sourcepath $(SRC) -classpath $(DEST) -d $(DEST)

$(DEST)/icons/%/$(wIDGET)$(P) :
	for i in $(ICONS); do\
		j=`dirname $$i`;\
		mkdir -p $(DEST)/icons/$$j;\
		cp -p $(RES)/icons/$$i $(DEST)/icons/$$i;\
	done

#	cp -p $(RES)/icons/mouse-l$(P) $(DEST)/icons;\
#	cp -p $(RES)/icons/mouse-r$(P) $(DEST)/icons

$(DEST)/sounds/% :
	mkdir -p $(DEST)/sounds
	cp $(RES)/sounds/*$(A) $(DEST)/sounds

$(DEST)/netscape/javascript/% : $(RES)/netscape/javascript/%
	@if test ! -d $(DEST)/netscape/javascript; then\
		cp -r $(RES)/netscape $(DEST)/netscape;\
	fi

$(DEST)/META-INF/MANIFEST.MF :
	mkdir -p $(DEST)/META-INF
	echo "Manifest-Version: 1.0" > $(DEST)/META-INF/MANIFEST.MF
	echo "Created-By: $(USER)@$(DOMAIN)" >> $(DEST)/META-INF/MANIFEST.MF
	echo "Permissions: sandbox" >> $(DEST)/META-INF/MANIFEST.MF
	echo "Codebase: www.$(DOMAIN) $(DOMAIN)" >> $(DEST)/META-INF/MANIFEST.MF
	echo "Application-Name: $(WIDGET)" >> $(DEST)/META-INF/MANIFEST.MF
	echo "Main-Class: $(PACKAGES)/$(wIDGET)/$(APPLET)" >> $(DEST)/META-INF/MANIFEST.MF

manifest : $(DEST)/META-INF/MANIFEST.MF

$(APP).jar : $(DEST)/META-INF/MANIFEST.MF $(TARGETS) $(RESOURCES)
	cd $(DEST);\
	jar cvmf META-INF/MANIFEST.MF ../$(APP).jar org/* $(PACKAGEBINARY);\
	chmod 644 ../$(APP).jar

jar : compile furnish $(APP).jar

sign :
	jarsigner $(APP).jar `signer`;\
	chmod 644 $(APP).jar

zip : jar sign
	cd ..; zip -rp $(wIDGET).zip $(PACKAGE) $(wIDGET)/$(APP).jar;\
	mv $(wIDGET).zip $(wIDGET)

pkzip :
	rm -rf $(wIDGET) $(wIDGET).zip;\
	mkdir $(wIDGET);\
	if [ -d $(wIDGET) -a -r $(wIDGET) -a -w $(wIDGET) -a -x $(wIDGET) ];\
	then\
		cp -rp $(PACKAGE) $(wIDGET);\
		cd ./$(wIDGET);\
		for i in $(PACKAGEASCII); do\
			cp $$i $$i.dos;\
			unix2dos $$i $$i.dos 2>/dev/null;\
			mv $$i.dos $$i;\
		done;\
		cd ..;\
		zip -rp $(wIDGET).zip $(wIDGET);\
	fi;\
	rm -rf $(wIDGET)

tar : prepare
	cd ..; tar cvf $(wIDGET).tar $(PACKAGE);\
	mv $(wIDGET).tar $(wIDGET)

compress : prepare
	cd ..; tar cvf - $(PACKAGE) | compress > $(wIDGET).tar.Z;\
	mv $(wIDGET).tar.Z $(wIDGET)

gzip : prepare
	cd ..; tar cvf - $(PACKAGE) | gzip > $(wIDGET).tar.gz;\
	mv $(wIDGET).tar.gz $(wIDGET)

bzip2 : prepare
	cd ..; tar cvf - $(PACKAGE) | bzip2 > $(wIDGET).tar.bz2;\
	mv $(wIDGET).tar.bz2 $(wIDGET)

xz : prepare
	cd ..; tar cvf - $(PACKAGE) | xz > $(wIDGET).tar.xz;\
	mv $(wIDGET).tar.xz $(wIDGET)

dos :
	for i in $(PACKAGEASCII); do\
		cp $$i $$i.dos
		unix2dos $$i $$i.dos
		mv $$i.dos $$i
	done

unix :
	for i in $(PACKAGEASCII); do\
		cp $$i $$i.unix;\
		dos2unix $$i $$i.unix;\
		mv $$i.unix $$i;\
	done

run : lee

test :
	$(NICE) java -jar $(APP).jar -lee=0 -control=0 -format=japanese -decimalPosition=0 -test=12;  true

lee : $(APP).jar
	$(NICE) java -jar $(APP).jar -lee=1; true

nolee : $(APP).jar
	$(NICE) java -jar $(APP).jar -rails=15; true

#Roman Hand Abacus (right most column twelfths and ancient Roman Numerals in display)
roman : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=roman -topPiece=2 -bottomPiece=6 -subdeck=3 -rails=10 -decimalPosition=2 -romanNumerals=1 -ancientRoman=1 -modernRoman=0 -latin=0 -museum=uk; true

#Alt Roman Hand Abacus (right most column eighths and modern Roman Numerals on abacus)
roman8 : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=roman -topPiece=2 -bottomPiece=6 -subdeck=3 -rails=10 -decimalPosition=2 -romanNumerals=1 -ancientRoman=0 -modernRoman=1 -eighth=1 -latin=0; true

#Russian Schoty
russian : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=russian -bottomPiece=4 -rails=11; true

#Old Russian Schoty
russianold : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=russian -bottomPiece=4 -bottomPiecePercent=4 -rails=11; true

#Georgian Schoty (not to be taken seriously)
georgian : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=russian -bottomPiece=4 -bottomPiecePercent=4 -rails=11 -base=20; true

#Danish School Abacus
danish : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=danish -rails=10 -decimalPosition=0 -group=1 -decimalComma=1; true

#Medieval Counter
medieval : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=medieval -rails=5 -decimalPosition=0 -romanNumerals=1 -ancientRoman=0; true

#Mesoamerican Nepohualtzintzin (similar to Japanese Soroban base 20)
mesoamerican : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=generic -diamond=1 -topNumber=3 -bottomNumber=4 -topSpaces=1 -bottomSpaces=1 -rails=13 -decimalPosition=0 -base=20 -anomaly=2 -vertical=1; true

#Babylonian Watch (proposed by author)
watch : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=generic -slot=1 -topNumber=1 -bottomNumber=4 -topSpaces=1 -bottomSpaces=1 -anomaly=4 -anomalySq=4; true

#Chinese solid-and-broken-bar system
bar : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=generic -topFactor=3 -topNumber=3 -bottomNumber=2 -decimalPosition=0 -base=12 -displayBase=12; true

#Base 16 Abacus
base16 : $(APP).jar
	$(NICE) java -jar $(APP).jar -format=japanese -base=16 -displayBase=10; true

clean :
	$(RM)r $(DEST)
	$(RM)r .sonar
	$(RM) sonar-project.properties

distclean : clean
	$(RM) *.zip *.xz *.bz2 *.gz *.Z *.tar *.jar *.jar.sign
	$(RM)r doc

prepareclean : distclean
	$(RM)r $(RES)/netscape

gcj : prepare init
	gcj -C -Wall -c -g -O $(SOURCES) -d $(DEST)
	gcj --main=$(APPLET) -o $(APPLET) $(APPLET)$(J)

antic :
	antic *$(J)

pmd :
	for i in *$(J); do\
		echo -n "$$i:";\
		pmd.sh $$i text unusedcode,imports,typeresolution,design,basic,coupling;\
	done
#codesize,controversial

checkstyle :
	for i in *$(J); do\
		echo -n "$$i:";\
		checkstyle.sh $$i | grep -v "contains a tab character";\
	done

jlint : $(TARGETS)
	jlint $(DEST)/*$(C) | grep -v "shadows one in base class"

findbugs : $(APP).jar
	findbugs -textui $(APP).jar | grep -v "Write to static field"

javadoc :
	mkdir -p doc; cd doc; javadoc ../*$(J)

sonar :
	echo "sonar.projectKey=$(wIDGET)" > sonar-project.properties
	echo "sonar.projectName=$(wIDGET)" >> sonar-project.properties
	echo "sonar.projectVersion=7" >> sonar-project.properties
	echo "sources=." >> sonar-project.properties
	echo "binaries=classes" >> sonar-project.properties
	echo "sonar.java.source=$(VER)" >> sonar-project.properties
	sonar-runner
