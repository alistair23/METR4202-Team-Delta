LIBS=ffmpeg-$FFMPEG_VERSION-win32-shared/bin/*.dll
7z x ffmpeg-$FFMPEG_VERSION-win32-shared.7z $LIBS
mkdir -p com/googlecode/javacv/cpp/windows-x86/
cp $LIBS com/googlecode/javacv/cpp/windows-x86/
jar cvf ffmpeg-$FFMPEG_VERSION-windows-x86.jar com/
rm -Rf com/
