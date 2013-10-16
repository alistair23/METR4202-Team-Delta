WGET="wget --content-disposition"

if [[ $1 == windows* ]]; then
    OPENCV_VERSION=2.4.6.0
    FFMPEG_VERSION=20130915-git-7ac6c63
    test -f OpenCV-$OPENCV_VERSION.exe || $WGET http://downloads.sourceforge.net/project/opencvlibrary/opencv-win/$OPENCV_VERSION/OpenCV-$OPENCV_VERSION.exe
    test -f ffmpeg-$FFMPEG_VERSION-win32-dev.7z || $WGET http://ffmpeg.zeranoe.com/builds/win32/dev/ffmpeg-$FFMPEG_VERSION-win32-dev.7z
    test -f ffmpeg-$FFMPEG_VERSION-win64-dev.7z || $WGET http://ffmpeg.zeranoe.com/builds/win64/dev/ffmpeg-$FFMPEG_VERSION-win64-dev.7z
    test -f ffmpeg-$FFMPEG_VERSION-win32-shared.7z || $WGET http://ffmpeg.zeranoe.com/builds/win32/shared/ffmpeg-$FFMPEG_VERSION-win32-shared.7z
    test -f ffmpeg-$FFMPEG_VERSION-win64-shared.7z || $WGET http://ffmpeg.zeranoe.com/builds/win64/shared/ffmpeg-$FFMPEG_VERSION-win64-shared.7z
else 
    OPENCV_VERSION=2.4.6.1
    FFMPEG_VERSION=2.0.1
    test -f opencv-$OPENCV_VERSION.tar.gz || $WGET https://github.com/Itseez/opencv/archive/$OPENCV_VERSION.tar.gz
    test -f ffmpeg-$FFMPEG_VERSION.tar.bz2 || $WGET http://ffmpeg.org/releases/ffmpeg-$FFMPEG_VERSION.tar.bz2
    test -f last_stable_x264.tar.bz2 || $WGET ftp://ftp.videolan.org/pub/videolan/x264/snapshots/last_stable_x264.tar.bz2
fi

source ./build_opencv-$1.sh
source ./build_ffmpeg-$1.sh

