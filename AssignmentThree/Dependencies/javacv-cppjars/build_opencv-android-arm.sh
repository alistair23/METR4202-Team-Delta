tar -xzvf opencv-$OPENCV_VERSION.tar.gz
mkdir opencv-$OPENCV_VERSION/build_android-arm
cd opencv-$OPENCV_VERSION
patch -p1 < ../opencv-$OPENCV_VERSION-android-arm.patch
cd build_android-arm
ANDROID_BIN=$ANDROID_NDK/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86_64/bin/ \
ANDROID_CPP=$ANDROID_NDK/sources/cxx-stl/gnu-libstdc++/4.6/ \
ANDROID_ROOT=$ANDROID_NDK/platforms/android-9/arch-arm/ \
cmake -DCMAKE_TOOLCHAIN_FILE=android.cmake -DCMAKE_INSTALL_PREFIX=$HOME/android -DBUILD_SHARED_LIBS=ON -DBUILD_TESTS=OFF -DBUILD_PERF_TESTS=OFF -DBUILD_ANDROID_EXAMPLES=OFF -DBUILD_JASPER=ON -DBUILD_JPEG=ON -DBUILD_OPENEXR=ON -DBUILD_PNG=ON -DBUILD_TBB=ON -DBUILD_TIFF=ON -DBUILD_ZLIB=ON -DBUILD_opencv_java=OFF -DBUILD_opencv_python=OFF -DENABLE_PRECOMPILED_HEADERS=OFF -DWITH_1394=OFF -DWITH_FFMPEG=OFF -DWITH_GSTREAMER=OFF -DWITH_TBB=ON -DWITH_CUDA=OFF -DWITH_OPENCL=OFF ..
make -j4
LIBS="lib/libopencv*.so lib/libtbb.so"
$ANDROID_NDK/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86_64/bin/arm-linux-androideabi-strip $LIBS
mkdir -p com/googlecode/javacv/cpp/android-arm/
cp $LIBS com/googlecode/javacv/cpp/android-arm/
jar cvf ../../opencv-$OPENCV_VERSION-android-arm.jar com/
rm -Rf com/
cd ../../
