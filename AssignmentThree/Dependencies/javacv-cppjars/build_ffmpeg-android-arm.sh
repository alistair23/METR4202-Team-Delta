ANDROID_BIN=$ANDROID_NDK/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86_64/bin/
ANDROID_ROOT=$ANDROID_NDK/platforms/android-9/arch-arm/
tar -xjvf ffmpeg-$FFMPEG_VERSION.tar.bz2
mv ffmpeg-$FFMPEG_VERSION ffmpeg-$FFMPEG_VERSION-android-arm
cd ffmpeg-$FFMPEG_VERSION-android-arm
tar -xjvf ../last_stable_x264.tar.bz2
X264=`echo x264-snapshot-*`
cd $X264
./configure --enable-static --enable-pic --disable-cli --cross-prefix=$ANDROID_BIN/arm-linux-androideabi- --sysroot=$ANDROID_ROOT --host=arm-linux --extra-cflags="-DANDROID -fPIC -ffunction-sections -funwind-tables -fstack-protector -march=armv7-a -mfloat-abi=softfp -mfpu=vfp -mfpu=neon -fomit-frame-pointer -fstrict-aliasing -funswitch-loops -finline-limit=300" --extra-ldflags="-nostdlib -Wl,--fix-cortex-a8 -lgcc -ldl -lz -lm -lc"
make -j4
cd ../
patch -p1 < ../ffmpeg-$FFMPEG_VERSION-android-arm.patch
./configure --prefix=$HOME/android --enable-shared --enable-gpl --enable-version3 --enable-runtime-cpudetect --disable-outdev=sdl --enable-libx264 --extra-cflags="-I$X264" --extra-ldflags="-L$X264" --enable-cross-compile --cc=$ANDROID_BIN/arm-linux-androideabi-gcc --sysroot=$ANDROID_ROOT --target-os=linux --arch=arm --extra-cflags="-DANDROID -fPIC -ffunction-sections -funwind-tables -fstack-protector -march=armv7-a -mfloat-abi=softfp -mfpu=vfp -mfpu=neon -fomit-frame-pointer -fstrict-aliasing -funswitch-loops -finline-limit=300" --extra-ldflags="-nostdlib -Wl,--fix-cortex-a8" --extra-libs="-lgcc -ldl -lz -lm -lc" --disable-stripping --disable-symver --disable-programs
make -j4
LIBS="libavcodec/libavcodec.so libavdevice/libavdevice.so libavfilter/libavfilter.so libavformat/libavformat.so libavutil/libavutil.so libpostproc/libpostproc.so libswresample/libswresample.so libswscale/libswscale.so"
$ANDROID_NDK/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86_64/bin/arm-linux-androideabi-strip $LIBS
mkdir -p com/googlecode/javacv/cpp/android-arm/
cp $LIBS com/googlecode/javacv/cpp/android-arm/
jar cvf ../ffmpeg-$FFMPEG_VERSION-android-arm.jar com/
rm -Rf com/
cd ../
