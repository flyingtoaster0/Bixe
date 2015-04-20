#!/bin/bash

MDPI=`ls|grep "\-mdpi"`
HDPI=`ls|grep "\-hdpi"`
XHDPI=`ls|grep "\-xhdpi"`
XXHDPI=`ls|grep "\-xxhdpi"`
XXXHDPI=`ls|grep "\-xxxhdpi"`

for f in $MDPI; do
    NEW_FILENAME=`echo $f|sed s/-mdpi//`
    echo "Moving $f to Bixe/src/main/res/drawable-mdpi/$NEW_FILENAME"
    mv $f Bixe/src/main/res/drawable-mdpi/$NEW_FILENAME
done

for f in $HDPI; do
    NEW_FILENAME=`echo $f|sed s/-hdpi//`
    echo "Moving $f to Bixe/src/main/res/drawable-hdpi/$NEW_FILENAME"
    mv $f Bixe/src/main/res/drawable-hdpi/$NEW_FILENAME
done

for f in $XHDPI; do
    NEW_FILENAME=`echo $f|sed s/-xhdpi//`
    echo "Moving $f to Bixe/src/main/res/drawable-xhdpi/$NEW_FILENAME"
    mv $f Bixe/src/main/res/drawable-xhdpi/$NEW_FILENAME
done

for f in $XXHDPI; do
    NEW_FILENAME=`echo $f|sed s/-xxhdpi//`
    echo "Moving $f to Bixe/src/main/res/drawable-xxhdpi/$NEW_FILENAME"
    mv $f Bixe/src/main/res/drawable-xxhdpi/$NEW_FILENAME
done

for f in $XXXHDPI; do
    NEW_FILENAME=`echo $f|sed s/-xxxhdpi//`
    echo "Moving $f to Bixe/src/main/res/drawable-xxxhdpi/$NEW_FILENAME"
    mv $f Bixe/src/main/res/drawable-xxxhdpi/$NEW_FILENAME
done

echo done
