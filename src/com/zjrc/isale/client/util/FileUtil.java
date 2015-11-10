package com.zjrc.isale.client.util;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：文件操作模块
 */

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileUtil {
	public static final String TAG = "FileUtil";
	public static final int FILECONTENT_MAXLENGTH = 10240;

	/**
	 * 创建文件目录
	 * 
	 * @param pathName
	 * @return
	 */
	public static void mkdirs(String pathName) {
		File file = new File(pathName);
		file.mkdirs();
	}

	/**
	 * 功能:判断文件是否存在
	 * 
	 * @param fileName
	 * @return boolean
	 */
	public static boolean exists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * 功能:获取文件路径
	 * 
	 * @param fullFileName
	 * @return String
	 */
	public static String getPath(String fullFileName) {
		int pos = fullFileName.lastIndexOf("/");
		if (pos == -1) {
			pos = fullFileName.lastIndexOf("\\");
		}
		String shortFileName = fullFileName.substring(0, pos);
		if (shortFileName == null || "".equalsIgnoreCase(shortFileName)) {
			return "/";
		}
		return shortFileName;
	}

	/**
	 * 功能:获取文件名
	 * 
	 * @param fullFileName
	 *            return String
	 */
	public static String getShortFileName(String fullFileName) {
		int pos = fullFileName.lastIndexOf("/");
		if (pos == -1) {
			pos = fullFileName.lastIndexOf("\\");
		}
		String shortFileName = fullFileName.substring(pos + 1,
				fullFileName.length());
		return shortFileName;
	}

	/**
	 * 功能:获取文件大小
	 * 
	 * @param filename
	 * @return long
	 */
	public static long getFileSize(String filename) {
		FileChannel fiChannel = null;
		long filesize;
		try {
			fiChannel = new FileInputStream(filename).getChannel();
			filesize = fiChannel.size();
		} catch (Exception e) {
			LogUtil.e("FileUtil", "getFileSize 出现错误,错误原因[" + e.getMessage()
					+ "]");
			filesize = 0;
		} finally {
			try {
				if (fiChannel != null) {
					fiChannel.close();
					fiChannel = null;
				}
			} catch (IOException ex) {
				LogUtil.e("FileUtil",
						"getFileSize close出现错误,错误原因[" + ex.getMessage() + "]");
			}
		}
		LogUtil.i("FileUtil", "getFileSize:" + filesize);
		return filesize;
	}

	/**
	 * 改文件名
	 * 
	 * @param sSrcFileName
	 * @param sDesFileName
	 * @return void
	 */
	public static void renameFile(String sSrcFileName, String sDesFileName)
			throws IOException {
		File srcfile = new File(sSrcFileName);
		File desfile = new File(sDesFileName);
		srcfile.renameTo(desfile);
	}

	/**
	 * 功能:读取文件所有内容 参数: filename:文件全路径 返回类型: ByteBuffer
	 */
	public static ByteBuffer readFile(String filename) {
		FileChannel fiChannel = null;
		try {
			fiChannel = new FileInputStream(filename).getChannel();
			MappedByteBuffer mBuf;
			mBuf = fiChannel.map(FileChannel.MapMode.READ_ONLY, 0,
					fiChannel.size());

			return mBuf;
		} catch (Exception e) {
			LogUtil.e("FileUtil", "readFile 出现错误,错误原因[" + e.getMessage() + "]");
			return null;
		} finally {
			try {
				if (fiChannel != null) {
					fiChannel.close();
					fiChannel = null;
				}
			} catch (IOException ex) {
				LogUtil.e("FileUtil",
						"readFile close出现错误,错误原因[" + ex.getMessage() + "]");
			}
		}
	}

	/**
	 * 功能:把文件读入byte数组 参数: filename:文件全路径 返回类型: ByteBuffer
	 */
	static public byte[] readAllFileToByte(String filename) {
		long len = 0;
		FileInputStream fin = null;

		try {
			File file = new File(filename);
			len = file.length();
			byte data[] = new byte[0];

			data = new byte[(int) len];

			fin = new FileInputStream(file);
			int r = fin.read(data);
			if (r != len)
				throw new IOException("Only read " + r + " of " + len + " for "
						+ file);

			return data;
		} catch (OutOfMemoryError ome) {
			LogUtil.e("FileUtil", "readAllFileToByte 分配内存(大小" + len
					+ ")出现错误,错误原因[" + ome.getMessage() + "]");
			return null;
		} catch (Exception ee) {
			LogUtil.e("FileUtil", "readAllFileToByte (大小" + len + ")出现错误,错误原因["
					+ ee.getMessage() + "]");
			return null;
		} finally {
			try {
				if (fin != null)
					fin.close();
			} catch (IOException ex) {
				LogUtil.e("FileUtil",
						"readAllFileToByte close出现错误,错误原因[" + ex.getMessage()
								+ "]");
			}
		}
	}

	/**
	 * 功能:读取文件从startpos开始长度为length的文件内容 参数: filename:文件全路径 startpos:开始位置
	 * length:长度 返回类型: ByteBuffer
	 */
	public static byte[] readFileToByte(String filename, long startpos,
			long length) {
		byte filebytes[] = new byte[0];
		RandomAccessFile randomFile = null;

		try {
			filebytes = new byte[(int) length];
			randomFile = new RandomAccessFile(filename, "r");
			randomFile.seek(startpos);
			randomFile.read(filebytes);

			return filebytes;
		} catch (OutOfMemoryError ome) {
			LogUtil.e("FileUtil", "readAllFileToByte 分配内存(大小" + length
					+ ")出现错误,错误原因[" + ome.getMessage() + "]");
			return null;
		} catch (Exception ee) {
			LogUtil.e("FileUtil", "readAllFileToByte (大小" + length
					+ ")出现错误,错误原因[" + ee.getMessage() + "]");
			return null;
		} finally {
			try {
				if (randomFile != null)
					randomFile.close();
			} catch (IOException ex) {
				LogUtil.e("FileUtil",
						"readFileToByte close出现错误,错误原因[" + ex.getMessage()
								+ "]");
			}
		}
	}

	/**
	 * 功能:读取文件从startpos开始长度为length的文件内容 参数: filename:文件全路径 startpos:开始位置
	 * length:长度 返回类型: ByteBuffer
	 */
	public static ByteBuffer readFile(String filename, long startpos,
			long length) {
		FileChannel fiChannel = null;
		try {
			fiChannel = new FileInputStream(filename).getChannel();
			MappedByteBuffer mBuf;
			mBuf = fiChannel.map(FileChannel.MapMode.READ_ONLY, startpos,
					length);

			return mBuf;
		} catch (Exception e) {
			LogUtil.e("FileUtil", "readFile2出现错误,错误原因[" + e.getMessage() + "]");
			return null;
		} finally {
			try {
				if (fiChannel != null) {
					fiChannel.close();
					fiChannel = null;
				}
			} catch (IOException ex) {
				LogUtil.e("FileUtil",
						"readFile2 close出现错误,错误原因[" + ex.getMessage() + "]");
			}
		}
	}

	/**
	 * 功能:读取文件所有内容并用Base64加密 参数: filename:文件全路径 返回类型: String
	 */
	public static String readFileAndEncode(String filename) throws IOException {
		String sBase64;
		sBase64 = Base64.encodeToString(readFile(filename).array(),
				Base64.DEFAULT);
		return sBase64;
	}

	/**
	 * 功能:读取文件从startpos开始长度为length的文件内容并用Base64加密 参数: filename:文件全路径
	 * startpos:开始位置 length:长度 返回类型: String
	 */
	public static String readFileAndEncode(String filename, long startpos,
			long length) throws IOException {
		String sBase64;
		sBase64 = Base64.encodeToString(
				readFileToByte(filename, startpos, length), Base64.DEFAULT);
		return sBase64;
	}

	/**
	 * 功能:向文件写内容 参数: filename:文件全路径' src:文件内容 返回类型: 无
	 */
	public static void writeFile(String filename, ByteBuffer src) {
		FileChannel foChannel = null;
		try {
			File file = new File(filename);
			if (file.getParent() != null) {
				mkdirs(file.getParent());
			}
			foChannel = new FileOutputStream(filename).getChannel();
			FileLock lock = foChannel.tryLock();
			if (lock != null) {
				foChannel.write(src);
				foChannel.force(true);
			}
			if (lock != null) {
				lock.release();
				lock = null;
			}

		} catch (Exception e) {
			LogUtil.e("FileUtil", "writeFile出现错误,错误原因[" + e.getMessage() + "]");
		} finally {
			try {
				if (foChannel != null) {
					foChannel.close();
					foChannel = null;
				}
			} catch (IOException ex) {
				LogUtil.e("FileUtil",
						"writeFile close出现错误,错误原因[" + ex.getMessage() + "]");
			}
		}
	}

	/**
	 * 功能:把byte数组写出到文件 参数: filename:文件全路径' src:文件内容 返回类型: 无
	 */
	static public void writeFile(String filename, byte data[]) {
		FileOutputStream fout = null;
		try {
			File file = new File(filename);
			if (file.getParent() != null) {
				mkdirs(file.getParent());
			}
			fout = new FileOutputStream(filename);
			fout.write(data);

		} catch (Exception e) {
			LogUtil.e("FileUtil", "writeFile2出现错误,错误原因[" + e.getMessage() + "]");
		} finally {
			try {
				if (fout != null)
					fout.close();
			} catch (IOException ex) {
				LogUtil.e("FileUtil",
						"writeFile2 close出现错误,错误原因[" + ex.getMessage() + "]");
			}
		}
	}

	/**
	 * 功能:从startpos开始长度为length向文件写文件内容 参数: filename:文件全路径 content:文件内容
	 * startpos:开始位置 length:长度 返回类型: ByteBuffer
	 */
	public static void writeFileFromByte(String filename, byte[] content,
			long startpos, long length) throws IOException {
		ByteBuffer src = ByteBuffer.wrap(content);
		writeFile(filename, src, startpos, length);
	}

	/**
	 * 功能:从startpos开始长度为length向文件写文件内容 参数: filename:文件全路径 src:文件内容 startpos:开始位置
	 * length:长度 返回类型: ByteBuffer
	 */
	public static void writeFile(String filename, ByteBuffer src,
			long startpos, long length) {
		FileChannel foChannel = null;
		try {
			File file = new File(filename);
			if (file.getParent() != null) {
				mkdirs(file.getParent());
			}
			foChannel = new RandomAccessFile(filename, "rw").getChannel();
			FileLock lock = foChannel.lock();
			if (lock != null) {
				foChannel.position(startpos);
				foChannel.write(src);
				foChannel.force(true);
			}
			if (lock != null) {
				lock.release();
				lock = null;
			}

		} catch (Exception e) {
			LogUtil.e("FileUtil", "writeFile3出现错误,错误原因[" + e.getMessage() + "]");
		} finally {
			try {
				if (foChannel != null) {
					foChannel.close();
					foChannel = null;
				}
			} catch (IOException ex) {
				LogUtil.e("FileUtil",
						"writeFile3 close出现错误,错误原因[" + ex.getMessage() + "]");
			}
		}
	}

	/**
	 * 功能:解压文件内容向文件写内容 参数: filename:文件全路径' src:文件内容 返回类型: 无
	 */
	public static void decodeAndWriteFile(String filename, String src)
			throws IOException {
		writeFile(filename, Base64.decode(src, Base64.DEFAULT));
	}

	/**
	 * 功能:解压文件内容并从startpos开始长度为length向文件写文件内容 参数: filename:文件全路径' src:文件内容 返回类型:
	 * 无
	 */
	public static void decodeAndWriteFile(String filename, String src,
			long startpos, long length) throws IOException {
		writeFileFromByte(filename, Base64.decode(src, Base64.DEFAULT),
				startpos, length);
	}

	// 增加删除文件和文件夹功能
	/**
	 * 功能:删除文件 参数: filename:文件全路径' 返回类型: 无
	 */
	public static void deleteFile(String filename) throws IOException {
		File file = new File(filename);
		if (file.isDirectory()) {
			throw new IOException(
					"IOException -> BadInputException: not a file.");
		}
		if (file.exists() == false) {
			throw new IOException(
					"IOException -> BadInputException: file is not exist.");
		}
		if (file.delete() == false) {
			throw new IOException("Cannot delete file. filename = " + filename);
		}
	}

	/**
	 * 功能:删除文件目录 参数: filedir:文件目录' 返回类型: 无
	 */
	public static void deleteDir(File filedir) throws IOException {
		if (filedir.isFile())
			throw new IOException(
					"IOException -> BadInputException: not a directory.");
		File[] files = filedir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isFile()) {
					file.delete();
				} else {
					deleteDir(file);
				}
			}
		}// if
		filedir.delete();
	}

	/**
	 * 获取下载图片完整路径
	 * 
	 * @param filename
	 * @return
	 */
	public static String getPicDownloadDir(String filename) {
		String filepath = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "isale" + File.separator + "download";
		filename = filepath + File.separator + filename;
		return filename;
	}

	/**
	 * 获取拍摄图片缓存路径
	 * 
	 * @param filename
	 * @return
	 */
	public static String getPicCaptureDir(String filename) {
		String filepath = Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "isale" + File.separator + "image";
		filename = filepath + File.separator + filename;
		return filename;
	}

	/**
	 * 判断图片是否在本地存在缓存
	 * 
	 * @param filename
	 * @return
	 */
	public static String hasPicCached(String filename) {
		String path = getPicDownloadDir(filename);
		File file = new File(path);
		if (file.exists()) {
			return path;
		}
		path = getPicCaptureDir(filename);
		file = new File(path);
		if (file.exists()) {
			return path;
		}
		return "";
	}

    /**
     * 获取下载文件夹路径
     */
    public static String getDownloadDir() {
        return Environment.getExternalStorageDirectory().getPath()
                + File.separator + "isale"
                + File.separator + "download"
                + File.separator;
    }
}
