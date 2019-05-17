package tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV操作(导出和导入)
 * 
 */
public class CSVUtils {

	/**
	 * 导出
	 * 
	 * @param file
	 *            csv文件(路径+文件名)，csv文件不存在会自动创建
	 * @param dataList
	 *            数据
	 * @return
	 */
	public static boolean exportCsv(File file, List<String> dataList) {
		boolean isSucess = false;

		FileOutputStream out = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			out = new FileOutputStream(file);
			osw = new OutputStreamWriter(out);
			bw = new BufferedWriter(osw);
			bw.append("序号,名称,方向,操作线,五日价,十日价,结构信息,止损点,止损比,止盈点,止盈比,开仓数,开仓比").append("\r");
			if (dataList != null && !dataList.isEmpty()) {
				for(int i=0;i<dataList.size();i++){
					bw.append((i+1)+",").append(dataList.get(i)).append("\r");
				}
//				for (String data : dataList) {
//					bw.append(data).append("\r");
//				}
			}
			isSucess = true;
		} catch (Exception e) {
			isSucess = false;
		} finally {
			if (bw != null) {
				try {
					bw.close();
					bw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (osw != null) {
				try {
					osw.close();
					osw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return isSucess;
	}

	/**
	 * 导入
	 * 
	 * @param file
	 *            csv文件(路径+文件)
	 * @return
	 */
	public static List<String> importCsv(File file) {
		List<String> dataList = new ArrayList<String>();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				dataList.add(line);
			}
		} catch (Exception e) {
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return dataList;
	}

	/**
	 * CSV导出
	 * 
	 * @throws Exception
	 */
	public void exportCsv() {
		List<String> dataList = new ArrayList<String>();
		dataList.add("1,张三,男");
		dataList.add("2,李四,男");
		dataList.add("3,小红,女");
		boolean isSuccess = CSVUtils.exportCsv(new File("D:/test/ljq.csv"),
				dataList);
		System.out.println(isSuccess);
	}

	/**
	 * CSV导出
	 * 
	 * @throws Exception
	 */
	public void importCsv() {
		List<String> dataList = CSVUtils.importCsv(new File("D:/test/ljq.csv"));
		if (dataList != null && !dataList.isEmpty()) {
			for (String data : dataList) {
				System.out.println(data);
			}
		}
	}

}