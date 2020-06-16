package dev.dokan.dokan_java;


import com.sun.jna.platform.win32.WinDef;
import org.joou.UInteger;
import org.joou.UShort;


public class UnsignedConversions {

    public static UShort fromNative(WinDef.USHORT value) {
        return UShort.valueOf(value.shortValue());
    }

    public static UShort fromPrimitive(@Unsigned short value) {
        return UShort.valueOf(value);
    }

    public static WinDef.USHORT toNative(UShort value) {
        return new WinDef.USHORT(value.shortValue());
    }

    public static void copyToNative(UShort value, WinDef.USHORT target) {
        target.setValue(value.shortValue());
    }

    public static @Unsigned short toPrimitive(UShort value) {
        return value.shortValue();
    }

    public static WinDef.USHORT primitiveToNative(@Unsigned short value) {
        return new WinDef.USHORT(value);
    }

    public static void copyPrimitiveToNative(@Unsigned short value, WinDef.USHORT target) {
        target.setValue(value);
    }

    public static @Unsigned short nativeToPrimitive(WinDef.USHORT value) {
        return value.shortValue();
    }

    public static UInteger fromNative(WinDef.ULONG value) {
        return UInteger.valueOf(value.intValue());
    }

    public static UInteger fromPrimitive(@Unsigned int value) {
        return UInteger.valueOf(value);
    }

    public static WinDef.ULONG toNative(UInteger value) {
        return new WinDef.ULONG(value.intValue());
    }

    public static void copyToNative(UInteger value, WinDef.ULONG target) {
        target.setValue(value.intValue());
    }

    public static @Unsigned int toPrimitive(UInteger value) {
        return value.intValue();
    }

    public static WinDef.ULONG primitiveToNative(@Unsigned int value) {
        return new WinDef.ULONG(value);
    }

    public static void copyPrimitiveToNative(@Unsigned int value, WinDef.ULONG target) {
        target.setValue(value);
    }

    public static @Unsigned int nativeToPrimitive(WinDef.ULONG value) {
        return value.intValue();
    }
}