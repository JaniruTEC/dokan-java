package dev.dokan.dokan_java.structure;


import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import dev.dokan.dokan_java.DokanNativeMethods;
import dev.dokan.dokan_java.Unsigned;
import dev.dokan.dokan_java.UnsignedConversions;
import dev.dokan.dokan_java.constants.dokany.MountOption;
import dev.dokan.dokan_java.masking.MaskValueSet;

import java.util.Arrays;
import java.util.List;


/**
 * Dokan mount options used to describe Dokan device behavior.
 *
 * @see <a href="https://dokan-dev.github.io/dokany-doc/html/struct_d_o_k_a_n___o_p_t_i_o_n_s.html">Dokany Documentation of PDOKAN_OPTIONS</a>
 */
public class DokanOptions extends Structure implements Structure.ByReference {

	/**
	 * Version of the Dokan features requested (version "123" is equal to Dokan version 1.2.3).
	 */
	public WinDef.USHORT Version = UnsignedConversions.toNative(DokanNativeMethods.getMinimumRequiredDokanVersion());

	/**
	 * Number of threads to be used internally by Dokan library. More thread will handle more events at the same time.
	 */
	public WinDef.USHORT ThreadCount;

	/**
	 * Features enable for the mount. It is a combination of {@link MountOption} masks.
	 */
	public WinDef.ULONG Options;

	/**
	 * FileSystem can store anything here
	 */ //FIXME Sure?!
	public WinDef.ULONGLONG GlobalContext = new WinDef.ULONGLONG(0L);

	/**
	 * Mount point. It can be a drive letter like \"M:\\\" or a folder path \"C:\\mount\\dokany\" on a NTFS partition.
	 */
	public WString MountPoint;

	/**
	 * UNC name used for the Network Redirector.
	 *
	 * @see <a href="https://docs.microsoft.com/de-de/windows-hardware/drivers/ifs/support-for-unc-naming-and-mup">Support for UNC Naming</a>
	 */
	public WString UNCName;

	/**
	 * Max timeout in milliseconds of each request before Dokan gives up to wait events to complete.
	 */
	public WinDef.ULONG Timeout;

	/**
	 * Allocation Unit Size of the volume. This will affect the file size.
	 */
	public WinDef.ULONG AllocationUnitSize;

	/**
	 * Sector Size of the volume. This will affect then file size.
	 */
	public WinDef.ULONG SectorSize;

	public DokanOptions() {

	}

	public DokanOptions(final String mountPoint, @Unsigned final short threadCount, final MaskValueSet<MountOption> mountOptions, final String uncName, @Unsigned final int timeout, @Unsigned final int allocationUnitSize, @Unsigned final int sectorSize) {
		MountPoint = new WString(mountPoint);
		ThreadCount = UnsignedConversions.primitiveToNative(threadCount);
		Options = UnsignedConversions.primitiveToNative(mountOptions.intValue());
		if (uncName != null) {
			UNCName = new WString(uncName);
		} else {
			UNCName = null;
		}
		Timeout = UnsignedConversions.primitiveToNative(timeout);
		AllocationUnitSize = UnsignedConversions.primitiveToNative(allocationUnitSize);
		SectorSize = UnsignedConversions.primitiveToNative(sectorSize);
	}

	public MaskValueSet<MountOption> getMountOptions() {
		return MaskValueSet.maskValueSet(UnsignedConversions.nativeToPrimitive(this.Options), MountOption.values());
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("Version", "ThreadCount", "Options", "GlobalContext", "MountPoint", "UNCName", "Timeout", "AllocationUnitSize", "SectorSize");
	}

	@Override
	public String toString() {
		return "DeviceOptions(Version=" + this.Version + ", ThreadCount=" + this.ThreadCount + ", Options=" + this.Options + ", mountOptions=" + this.getMountOptions() + ", GlobalContext=" + this.GlobalContext + ", MountPoint=" + this.MountPoint + ", UNCName=" + this.UNCName + ", Timeout=" + this.Timeout + ", AllocationUnitSize=" + this.AllocationUnitSize + ", SectorSize=" + this.SectorSize + ")";
	}
}
