//
// 解密工具
// Created by yang on 18-4-17.
//

#include <iostream>
#include <jvmti.h>
#include <cstring>
#include "Util.h"


void JNICALL
ClassFileLoadHook(
        jvmtiEnv *jvmti_env,
        JNIEnv *jni_env,
        jclass class_being_redefined,
        jobject loader,
        const char *name,
        jobject protection_domain,
        jint class_data_len,
        const unsigned char *class_data,
        jint *new_class_data_len,
        unsigned char **new_class_data
) {
    //实现class文件的解密
    //这代表正常的字节码
    /*if (Util::byte2Int(class_data) == 0xbebafeca) {
        *new_class_data_len = class_data_len;
        jvmti_env->Allocate(class_data_len, new_class_data);
        memcpy(*new_class_data, class_data, static_cast<size_t>(class_data_len));
        return;
    }
    //代表被加密了的字节码
    *new_class_data_len = class_data_len - 4;
    //分配内存
    jvmti_env->Allocate(*new_class_data_len, new_class_data);
    memcpy(*new_class_data, class_data + 4, static_cast<size_t>(*new_class_data_len));*/
    Array<byte> array = Util::decode(class_data, class_data_len);
    *new_class_data_len = array.length;
    *new_class_data = array.source;
}

/**
 * 在agent加载完成执行
 * @param vm  java虚拟机
 * @param options
 * @param reserved
 * @return
 */
JNIEXPORT  jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    // nothing to do
    jvmtiEnv *jvmti = NULL;
    jvmtiError error;
    //获取jvm的环境
    jint result = jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_1);
    if (result != JNI_OK) {
        printf("ERROR: Unable to access JVMTI!\n");
    }
    //方便判断SetEventNotificationMode是否要开启
    jvmtiCapabilities capabilities;

    (void) memset(&capabilities, 0, sizeof(capabilities));
    capabilities.can_generate_all_class_hook_events = 1;
    capabilities.can_tag_objects = 1;
    capabilities.can_generate_object_free_events = 1;
    capabilities.can_get_source_file_name = 1;
    capabilities.can_get_line_numbers = 1;
    capabilities.can_generate_vm_object_alloc_events = 1;

    error = jvmti->AddCapabilities(&capabilities);
    //有错误就返回
    if (error != JVMTI_ERROR_NONE) {
        printf("ERROR: Unable to AddCapabilities JVMTI!\n");
        return error;
    }

    //设置事件回调
    jvmtiEventCallbacks callbacks;
    (void) memset(&callbacks, 0, sizeof(callbacks));

    callbacks.ClassFileLoadHook = &ClassFileLoadHook;
    error = jvmti->SetEventCallbacks(&callbacks, sizeof(callbacks));
    if (JVMTI_ERROR_NONE != error) {
        printf("ERROR: Unable to SetEventCallbacks JVMTI!\n");
        return error;
    }

    //设置事件通知
    error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, NULL);
    if (JVMTI_ERROR_NONE != error) {
        printf("ERROR: Unable to SetEventNotificationMode JVMTI!\n");
        return error;
    }

    return JNI_OK;
}


JNIEXPORT void JNICALL Agent_OnUnload(JavaVM *vm) {
    // nothing to do
}



