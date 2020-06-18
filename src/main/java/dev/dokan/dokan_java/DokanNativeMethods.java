package dev.dokan.dokan_java;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import dev.dokan.dokan_java.constants.dokany.MountError;
import dev.dokan.dokan_java.structure.DokanControl;
import dev.dokan.dokan_java.structure.DokanFileInfo;
import dev.dokan.dokan_java.structure.DokanOptions;


/**
 * Native API to the kernel Dokan driver.
 */
public class DokanNativeMethods implements StdCallLibrary {

	private static final String DOKAN_DLL = "dokan1";

	private static final short MINIMUM_REQUIRED_DOKAN_VERSION = 130;

	private static final int REQUIRED_WIN_BYTE_SIZE = 1;
	private static final int REQUIRED_WIN_SHORT_SIZE = 2;
	private static final int REQUIRED_WIN_INT_SIZE = 4;
	private static final int REQUIRED_WIN_LONG_SIZE = 8;
	private static final int REQUIRED_WIN_POINTER_SIZE = REQUIRED_WIN_LONG_SIZE;
	private static final String TEST_SIZE_FAILED_MESSAGE = "Critical Error: dokan-java requires %s to be %d bytes long, but got %d! " +
			"This dokan-java release cannot be run on this computer. dokan-java is required for this program to work. " +
			"Please contact the author of the program or the dokan-java-team at https://www.github.com/dokan-dev/dokan-java";

	static {
		checkSizes();
		Native.register(DOKAN_DLL);
	}

	private DokanNativeMethods() {

	}

	/**
	 * Returns the hard coded minimum required dokan driver version that is needed by this library.
	 * <p>
	 * The version is returned in "Dokan" style, i.e. without any dots.
	 *
	 * @return the minimum required dokan version without dots.
	 */
	public static short getMinimumRequiredDokanVersion() {
		return MINIMUM_REQUIRED_DOKAN_VERSION;
	}

	/**
	 * Mount a new Dokan Volume. This function blocks until the device is unmounted.
	 * If the mount fails, it will directly return {@link MountError}.
	 *
	 * @param options A {@link DokanOptions} object that describes the mount.
	 * @param operations Instance of {@link DokanOperations} that will be called for each file system request made by the kernel.
	 * @return a status code indicating the outcome. For the possible values, see {@link MountError}.
	 */
	static native int DokanMain(DokanOptions options, DokanOperations operations);

	/**
	 * Get the version of Dokan.
	 *
	 * <p>The returned long value is the version number without the dots.</p>
	 *
	 * @return The version of Dokan
	 */
	static native long DokanVersion();

	/**
	 * Get the version of the Dokan driver.
	 *
	 * <p>The returned long value is the version number without the dots.</p>
	 *
	 * @return The version of Dokan driver.
	 */
	static native long DokanDriverVersion();

	/**
	 * Unmount a Dokan device from a driver letter.
	 *
	 * @param driveLetter Driver letter to unmount.
	 * @return {@code true} if device was unmounted or {@code false} in case of failure or device not found.
	 */
	static native boolean DokanUnmount(char driveLetter);

	/**
	 * Unmount a Dokan device from a mount point
	 *
	 * @param mountPoint Mount point to unmount
	 * <ul>
	 * <li>Z</li>
	 * <li>Z:</li>
	 * <li>Z:\\</li>
	 * <li>Z:\MyMountPoint</li>
	 * </ul>
	 * @return {@code true} if device was unmounted or {@code false} in case of failure or device not found.
	 */
	static native boolean DokanRemoveMountPoint(WString mountPoint);


	/**
	 * TODO: Does not work. Why?
	 * static native boolean DokanRemoveMountPointEx(WString mountPoint, boolean safe);
	 */

	/**
	 * Extends the time out of the current IO operation in driver.
	 *
	 * @param timeout Extended time in milliseconds requested.
	 * @param dokanFileInfo {@link DokanFileInfo} of the operation to extend.
	 * @return {@code true} if the operation was successful, otherwise false.
	 */
	static native boolean DokanResetTimeout(long timeout, DokanFileInfo dokanFileInfo);

	/**
	 * Get the handle to Access Token.
	 *
	 * <p>
	 * This method needs be called in <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa363858(v=vs.85).aspx">CreateFile</a> callback.
	 * The caller must call <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms724211(v=vs.85).aspx">CloseHandle</a> for the returned handle.
	 * </p>
	 *
	 * @param dokanFileInfo {@link DokanFileInfo} of the operation to extend.
	 * @return A handle to the account token for the user on whose behalf the code is running.
	 */
	static native WinNT.HANDLE DokanOpenRequestorToken(DokanFileInfo dokanFileInfo);

	/**
	 * Convert {@link DokanOperations.ZwCreateFile} parameters to CreateFile parameters.
	 * TODO: Improve documentation
	 *
	 * @param desiredAccess
	 * @param fileAttributes FileAttributes
	 * @param createOptions CreateOptions
	 * @param createDisposition CreateDisposition
	 * @param genericDesiredAccess
	 * @param outFileAttributesAndFlags
	 * @param outCreationDisposition
	 */
	static native void DokanMapKernelToUserCreateFileFlags(
			long desiredAccess,
			long fileAttributes,
			long createOptions,
			long createDisposition,
			IntByReference genericDesiredAccess,
			IntByReference outFileAttributesAndFlags,
			IntByReference outCreationDisposition);

	/**
	 * Checks whether name  matches Expression.
	 * <p>
	 * Behave like FsRtlIsNameInExpression routine from <a href="https://msdn.microsoft.com/en-us/library/ff546850(v=VS.85).aspx">Microsoft</a>\n
	 * </p>
	 * <table class="striped">
	 * <caption style="display:none">Special character handling</caption>
	 * <thead>
	 * <tr>
	 * <th scope="col">Character</th>
	 * <th scope="col">Character description</th>
	 * <th scope="col">Effect</th>
	 * </tr>
	 * </thead>
	 * <tbody>
	 * <tr>
	 * <td>*</th>
	 * <td>asterisk</th>
	 * <td>Matches zero or more characters.</td>
	 * </tr>
	 * <tr>
	 * <td>?</th>
	 * <td>question mark</th>
	 * <td>Matches a single character.</td>
	 * </tr>
	 * <tr>
	 * <td>"</th>
	 * <td>quotation mark, DOS_DOT</th>
	 * <td>Matches either a period or zero characters beyond the name string.</td>
	 * </tr>
	 * <tr>
	 * <td>{@literal >}</th>
	 * <td>greater than, DOS_QM</th>
	 * <td>Matches any single character or, upon encountering a period or end of name string, advances the expression to the end of the set of contiguous DOS_QMs.</td>
	 * </tr>
	 * <tr>
	 * <td>{@literal <}</th>
	 * <td>less than, DOS_STAR</th>
	 * <td>Matches zero or more characters until encountering and matching the final . in the name.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 *
	 * @param expression - Expression, possibly containing any of the above characters
	 * @param name - Name to check
	 * @param ignoreCase - Case sensitive or not
	 * @return {@code true} if name matches the expression, otherwise false
	 */
	static native boolean DokanIsNameInExpression(WString expression, WString name, boolean ignoreCase);

	/**
	 * Get active Dokan mount points.
	 *
	 * <p>
	 * Returned array need to be released by calling {@link #DokanReleaseMountPointList}.
	 * </p>
	 *
	 * @param uncOnly - Get only instances that have UNC Name.
	 * @param nbRead - {@link Unsigned} Number of instances successfully retrieved
	 * @return a pointer to the start of the allocated array of {@link DokanControl} elemets.
	 */
	static native Pointer DokanGetMountPointList(boolean uncOnly, @Unsigned IntByReference nbRead);

	/**
	 * Release Mount point list resources from {@link #DokanGetMountPointList}.
	 *
	 * <p>
	 * After {@link #DokanGetMountPointList} call you will receive a dynamically allocated array of {@link DokanControl}.
	 * This array needs to be released when no longer needed by calling this function.
	 * </p>
	 *
	 * @param startOfList Pointer to the start of the {@link DokanControl} list.
	 */
	static native void DokanReleaseMountPointList(Pointer startOfList);

	/**
	 * Convert Win32 error to NtStatus
	 *
	 * @param error - Win32 error to convert
	 * @return NtStatus associated to the error
	 * @see <a href="https://support.microsoft.com/en-us/kb/113996">kb113996</a>
	 */
	static native long DokanNtStatusFromWin32(int error);

	/**
	 * Notify Dokan that a file or a directory has been created.
	 *
	 * @param FilePath Absolute path to the file or directory, including the mount-point of the file system.
	 * @param IsDirectory Indicates if the path is a directory.
	 * @return {@code true} if notification succeeded.
	 */
	static native boolean DokanNotifyCreate(WString FilePath, boolean IsDirectory);

	/**
	 * Notify Dokan that a file or a directory has been deleted.
	 *
	 * @param FilePath Absolute path to the file or directory, including the mount-point of the file system.
	 * @param IsDirectory Indicates if the path was a directory.
	 * @return {@code true} if notification succeeded.
	 */
	static native boolean DokanNotifyDelete(WString FilePath, boolean IsDirectory);

	/**
	 * Notify Dokan that file or directory attributes have changed.
	 *
	 * @param FilePath Absolute path to the file or directory, including the mount-point of the file system.
	 * @return {@code true} if notification succeeded.
	 */
	static native boolean DokanNotifyUpdate(WString FilePath);

	/**
	 * Notify Dokan that file or directory extended attributes have changed.
	 *
	 * @param FilePath Absolute path to the file or directory, including the mount-point of the file system.
	 * @return {@code true} if notification succeeded.
	 */
	static native boolean DokanNotifyXAttrUpdate(WString FilePath);

	/**
	 * Notify Dokan that a file or a directory has been renamed.
	 * <p>
	 * This method supports in-place rename for file/directory within the same parent.
	 *
	 * @param OldPath Old, absolute path to the file or directory, including the mount-point of the file system.
	 * @param NewPath New, absolute path to the file or directory, including the mount-point of the file system.
	 * @param IsDirectory Indicates if the path is a directory.
	 * @param IsInSameDirectory Indicates if the file or directory have the same parent directory.
	 * @return {@code true} if notification succeeded.
	 */
	static native boolean DokanNotifyRename(WString OldPath, WString NewPath,
											boolean IsDirectory, boolean IsInSameDirectory);

	static void checkSizes() {
		checkSize(WinDef.CHAR.SIZE, REQUIRED_WIN_BYTE_SIZE, "Win CHAR");
		checkSize(WinDef.BYTE.SIZE, REQUIRED_WIN_BYTE_SIZE, "Win BYTE");
		checkSize(WinDef.USHORT.SIZE, REQUIRED_WIN_SHORT_SIZE, "Win USHORT");
		checkSize(WinDef.SHORT.SIZE, REQUIRED_WIN_SHORT_SIZE, "Win SHORT");
		checkSize(WinDef.DWORD.SIZE, REQUIRED_WIN_INT_SIZE, "Win DWORD");
		checkSize(WinDef.ULONG.SIZE, REQUIRED_WIN_INT_SIZE, "Win ULONG");
		checkSize(WinDef.LONG.SIZE, REQUIRED_WIN_INT_SIZE, "Win LONG");
		checkSize(WinDef.LONGLONG.SIZE, REQUIRED_WIN_LONG_SIZE, "Win LONGLONG");
		checkSize(WinDef.ULONGLONG.SIZE, REQUIRED_WIN_LONG_SIZE, "Win ULONGLONG");

		checkSize(Native.LONG_SIZE, REQUIRED_WIN_INT_SIZE, "Native LONG");
		checkSize(Native.POINTER_SIZE, REQUIRED_WIN_LONG_SIZE, "Native POINTER");
	}

	static void checkSize(int given, int required, String typeName) {
		if(given != required) {
			throw new AssertionError(String.format(TEST_SIZE_FAILED_MESSAGE, typeName, required, given));
		}
	}
}