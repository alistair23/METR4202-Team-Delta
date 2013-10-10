tar -xzvf opencv-$OPENCV_VERSION.tar.gz
mkdir opencv-$OPENCV_VERSION/build_macosx-x86_64
cd opencv-$OPENCV_VERSION
patch -p1 < ../opencv-$OPENCV_VERSION-macosx-x86_64.patch
cd build_macosx-x86_64
cmake -DBUILD_TESTS=OFF -DBUILD_PERF_TESTS=OFF -DBUILD_JASPER=ON -DBUILD_JPEG=ON -DBUILD_OPENEXR=ON -DBUILD_PNG=ON -DBUILD_TBB=ON -DBUILD_TIFF=ON -DBUILD_ZLIB=ON -DBUILD_opencv_java=OFF -DBUILD_opencv_python=OFF -DENABLE_PRECOMPILED_HEADERS=OFF -DWITH_1394=OFF -DWITH_FFMPEG=OFF -DWITH_GSTREAMER=OFF -DWITH_TBB=ON -DWITH_CUDA=OFF -DWITH_OPENCL=OFF -DCMAKE_SHARED_LINKER_FLAGS="-Wl,-headerpad_max_install_names" ..
make -j4
VER=${OPENCV_VERSION:0:3}
LIBS="lib/libopencv*.$VER.dylib lib/libtbb.dylib"
strip $LIBS
BADPATH=`pwd`/lib
for f in $LIBS; do install_name_tool $f -id @rpath/`basename $f` \
        -add_rpath /usr/local/lib/ -add_rpath /opt/local/lib/ -add_rpath @loader_path/. \
        -change $BADPATH/libtbb.dylib @rpath/libtbb.dylib \
        -change $BADPATH/libopencv_core.$VER.dylib @rpath/libopencv_core.$VER.dylib \
        -change $BADPATH/libopencv_calib3d.$VER.dylib @rpath/libopencv_calib3d.$VER.dylib \
        -change $BADPATH/libopencv_features2d.$VER.dylib @rpath/libopencv_features2d.$VER.dylib \
        -change $BADPATH/libopencv_flann.$VER.dylib @rpath/libopencv_flann.$VER.dylib \
        -change $BADPATH/libopencv_gpu.$VER.dylib @rpath/libopencv_gpu.$VER.dylib \
        -change $BADPATH/libopencv_highgui.$VER.dylib @rpath/libopencv_highgui.$VER.dylib \
        -change $BADPATH/libopencv_imgproc.$VER.dylib @rpath/libopencv_imgproc.$VER.dylib \
        -change $BADPATH/libopencv_legacy.$VER.dylib @rpath/libopencv_legacy.$VER.dylib \
        -change $BADPATH/libopencv_ml.$VER.dylib @rpath/libopencv_ml.$VER.dylib \
        -change $BADPATH/libopencv_nonfree.$VER.dylib @rpath/libopencv_nonfree.$VER.dylib \
        -change $BADPATH/libopencv_objdetect.$VER.dylib @rpath/libopencv_objdetect.$VER.dylib \
        -change $BADPATH/libopencv_photo.$VER.dylib @rpath/libopencv_photo.$VER.dylib \
        -change $BADPATH/libopencv_video.$VER.dylib @rpath/libopencv_video.$VER.dylib; done
mkdir -p com/googlecode/javacv/cpp/macosx-x86_64/
cp $LIBS com/googlecode/javacv/cpp/macosx-x86_64/
jar cvf ../../opencv-$OPENCV_VERSION-macosx-x86_64.jar com/
rm -Rf com/
cd ../../
