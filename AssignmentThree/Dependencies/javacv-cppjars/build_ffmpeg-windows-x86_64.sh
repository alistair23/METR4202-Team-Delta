LIBS=ffmpeg-$FFMPEG_VERSION-win64-shared/bin/*.dll
7z x ffmpeg-$FFMPEG_VERSION-win64-shared.7z $LIBS
mkdir -p com/googlecode/javacv/cpp/windows-x86_64/
cp $LIBS com/googlecode/javacv/cpp/windows-x86_64/
jar cvf ffmpeg-$FFMPEG_VERSION-windows-x86_64.jar com/
rm -Rf com/
