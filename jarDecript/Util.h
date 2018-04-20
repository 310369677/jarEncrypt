//
// Created by yang on 18-4-18.
//

#ifndef JARDECRIPT_UTIL_H
#define JARDECRIPT_UTIL_H

typedef unsigned char byte;

template <class T>
struct Array{
    T *source;
    int length;
};

class Util {
public:
    /**
     * 解码
     * @param array 数组
     * @return 解码后的数组
     */
    static Array<byte> decode(Array<byte> array);
    static Array<byte> decode(const byte* source, int length);
    template<typename T> static Array<T> emptyArray(T);
    static int byte2Int(const byte* bytes, int size=4);

private:
    Util();

    static void writeClassHead(byte *result);

    static void readClassContent(Array<byte> source, Array<byte> result, byte val, byte fillCount, int classContentLength);
};


#endif //JARDECRIPT_UTIL_H
