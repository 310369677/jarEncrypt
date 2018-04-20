//
// Created by yang on 18-4-18.
//

#include <cstring>
#include "Util.h"

Array<byte> Util::decode(Array<byte> array) {
    if (array.length < 4) {
        throw "数组的长度不能小于4";
    }
    if (byte2Int(array.source) == 0xbebafeca) {
        Array<byte> newArray = {source:nullptr, length:array.length};
        newArray.source = new byte[newArray.length];
        memcpy(newArray.source, array.source, static_cast<size_t>(newArray.length));
        return array;
    }
    byte *source = array.source;
    //开始解码
    byte fillCount = source[0] + source[1] - source[2] - source[3];
    //跳转的基数
    byte val = source[4];
    //原始字节码码的长度
    int classContentLength = array.length - 5 - fillCount;
    byte *result = new byte[classContentLength + 4];
    //写入class的头
    writeClassHead(result);
    Array<byte> newArray = {source:result, length:classContentLength + 4};
    readClassContent(array,newArray,val,fillCount,classContentLength);
    return newArray;
}


Array<byte> Util::decode(const byte *source, int length) {
    if (source == nullptr || length == 0) {
        byte a = 0;
        return emptyArray(a);
    }
    Array<byte> array = {source:const_cast<byte *>(source), length:length};
    return decode(array);
}

template<typename T>
Array<T> Util::emptyArray(T t) {
    Array<T> array = {source:nullptr, length:0};
    return array;
}

int Util::byte2Int(const byte *bytes, int size) {
    if (size != 4) {
        throw "this is not a int";
    }
    int a = bytes[0] & 0xFF;

    a |= ((bytes[1] << 8) & 0xFF00);

    a |= ((bytes[2] << 16) & 0xFF0000);

    a |= ((bytes[3] << 24) & 0xFF000000);
    return a;
}

void Util::writeClassHead(byte *result) {
    //0xbebafeca
    result[0] = (byte) 0xca;
    result[1] = (byte) 0xfe;
    result[2] = (byte) 0xba;
    result[3] = (byte) 0xbe;
}

void Util::readClassContent(Array<byte> source, Array<byte> result, byte val, byte fillCount, int classContentLength) {
    int count = classContentLength / val;
    int contentFillCount = count < fillCount ? count : fillCount;
    int resultPos = 4;
    int sourcePos = 5;
    for (int i = 0; i < contentFillCount; i++){
        memcpy(result.source+resultPos,source.source+sourcePos,val);
        sourcePos+=val;
        resultPos+=val;
        sourcePos++;
    }
    if(count<fillCount){
        memcpy(result.source+resultPos,source.source+sourcePos, static_cast<size_t>(result.length - resultPos));
    } else{
        memcpy(result.source+resultPos,source.source+sourcePos, static_cast<size_t>(source.length - sourcePos));
    }
}

Util::Util() = default;